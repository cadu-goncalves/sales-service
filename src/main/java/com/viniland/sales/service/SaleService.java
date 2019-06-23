package com.viniland.sales.service;

import com.viniland.sales.component.SpringContext;
import com.viniland.sales.component.kafka.CashbackCreditProducer;
import com.viniland.sales.domain.event.CashbackCreditEvent;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.exception.DomainException;
import com.viniland.sales.domain.exception.SaleException;
import com.viniland.sales.domain.exception.SaleItemException;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.model.CashbackOffer;
import com.viniland.sales.domain.model.Sale;
import com.viniland.sales.domain.rest.SaleResource;
import com.viniland.sales.domain.rest.filter.SaleFilter;
import com.viniland.sales.persistence.AlbumRepository;
import com.viniland.sales.persistence.CashbackOfferRepository;
import com.viniland.sales.persistence.SaleRepository;
import com.viniland.sales.util.MessageUtils;
import com.viniland.sales.util.WeekDayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaleService {

    private static MathContext MATH_CONTEXT = new MathContext(4, RoundingMode.HALF_UP);

    private final TaskExecutor executor;

    private final SaleRepository repository;

    private final AlbumRepository albumRepository;

    private final CashbackCreditProducer producer;

    private Map<CashbackOffer.WeekDay, Map<String, Double>> offers;

    public SaleService(TaskExecutor executor, SaleRepository repository,
                       AlbumRepository albumRepository, CashbackCreditProducer producer) {
        this.executor = executor;
        this.repository = repository;
        this.albumRepository = albumRepository;
        this.producer = producer;
        offers = new HashMap<>();
    }

    /**
     * Retrieve sale
     *
     * @param id sale unique identifier
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Sale> retrieve(String id) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Retrieve sale: {}", id);

            Optional<Sale> findResult = repository.findById(id);
            if (!findResult.isPresent()) {
                // Not found
                String message = MessageUtils.getMessage("messages", "sale.notfound");
                throw new SaleException(message, DomainError.RETRIEVE_ERROR);
            }

            return findResult.get();
        }, executor).exceptionally(throwable -> {
            log.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Search multiple sales
     *
     * @param filter {@link SaleFilter}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Page<Sale>> search(final SaleFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Search albums: {}", filter);

            // Check date range
            if (Objects.nonNull(filter.getFrom()) && Objects.nonNull(filter.getTo())) {
                if (!filter.getFrom().before(filter.getTo())) {
                    String message = MessageUtils.getMessage("messages", "filter.date.range.invalid");
                    throw new SaleException(message, DomainError.RETRIEVE_ERROR);
                }
            }

            // Sort & Pagination
            Sort sort = Sort.by(Sort.Direction.DESC, "register");
            PageRequest page = PageRequest.of(filter.getPage(), filter.getSize(), sort);

            // Find
            if (Objects.isNull(filter.getFrom()) && Objects.isNull(filter.getTo())) {
                // No range
                return repository.findAll(page);
            } else if (Objects.isNull(filter.getFrom())) {
                // To date
                return repository.findByRegisterTo(filter.getTo(), page);
            } else if (Objects.isNull(filter.getTo())) {
                // From date
                return repository.findByRegisterFrom(filter.getFrom(), page);
            } else {
                // Between dates
                return repository.findByRegisterBetween(filter.getFrom(), filter.getTo(), page);
            }
        }, executor).exceptionally(throwable -> {
            log.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Register sale
     *
     * @param resource {@link SaleResource}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Sale> register(final SaleResource resource) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Register sale: {}", resource);
            // Compute items prices/cashbacks
            Map<String, Sale.SaleItem> items = computeItems(resource);
            // Compute sale based on items
            Sale sale = computeSale(items, resource);
            // Register sale/items
            sale = repository.save(sale);
            // Emit credit
            producer.send(new CashbackCreditEvent(sale.getCustomerId(), sale.getCashback()));
            return sale;
        }, executor).exceptionally(throwable -> {
            log.error(throwable.getMessage());
            throw translateException(throwable);
        });
    }

    /**
     * Compute sale totals.
     *
     * @param resource
     * @return
     */
    private Sale computeSale(Map<String, Sale.SaleItem> items, SaleResource resource) {
        // Compute sale purchase/cashback
        AtomicReference<BigDecimal> total = new AtomicReference<>(new BigDecimal(0, MATH_CONTEXT));
        AtomicReference<BigDecimal> casback = new AtomicReference<>(new BigDecimal(0, MATH_CONTEXT));
        items.values().stream().forEach(item -> {
            total.accumulateAndGet(item.computePrice(MATH_CONTEXT), (v1, v2) -> v1.add(v2));
            casback.accumulateAndGet(item.computeCashback(MATH_CONTEXT), (v1, v2) -> v1.add(v2));
        });

        return Sale.builder()
                .register(resource.getRegister())
                .customerId(resource.getCustomerId())
                .total(total.get().doubleValue())
                .cashback(casback.get().doubleValue())
                .items(items.values())
                .build();
    }

    /**
     * Compute sold items.
     *
     * @param resource
     * @return
     */
    private Map<String, Sale.SaleItem> computeItems(SaleResource resource) {
        if (offers.isEmpty()) {
            // Retry
            retrieveOffers();
        }

        // Sale week day
        CashbackOffer.WeekDay weekDay = WeekDayUtils.fromDate(resource.getRegister());

        // Sale items
        Map<String, Sale.SaleItem> items = resource.getItems().stream()
                .collect(Collectors.toMap(
                        SaleResource.SaleItemResource::getId,
                        i -> Sale.SaleItem.builder()
                                .id(i.getId())
                                .quantity(i.getQuantity())
                                .build()
                ));

        // Recover albums
        Collection<Album> albums = (Collection<Album>) albumRepository.findAllById(items.keySet());
        if (albums.size() != items.keySet().size()) {
            // Abort sale, informing items not found
            Set<String> found = albums.stream().map(Album::getId).collect(Collectors.toSet());
            Set<String> notFound = new HashSet<>(items.keySet());
            notFound.removeAll(found);
            String fmtMessage = MessageUtils.getMessage("messages", "sale.item.notfound");
            String message = MessageFormat.format(fmtMessage, String.join(",", notFound));
            throw new SaleItemException(message, DomainError.CREATE_ERROR);
        }

        // Associate cashback/price based on weekday/genre
        albums.forEach(album -> {
            Sale.SaleItem item = items.get(album.getId());
            item.setPrice(album.getPrice());

            Double cashback = offers.get(weekDay).get(album.getGenre().toLowerCase());
            BigDecimal valueCaskback = new BigDecimal(album.getPrice(), MATH_CONTEXT)
                    .multiply(BigDecimal.valueOf(cashback))
                    .divide(BigDecimal.valueOf(100L));
            item.setCashback(valueCaskback.doubleValue());

            items.put(album.getId(), item);
        });

        return items;
    }


    /**
     * Translate relevant exceptions into {@link DomainException}
     *
     * @param throwable {@link Throwable} original exception
     * @return {@link SaleException}
     */
    private DomainException translateException(Throwable throwable) {
        String message;
        DomainException exception;

        // Unwrap
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        // Check
        if (throwable instanceof DomainException) {
            // Nothing to do
            return (DomainException) throwable;
        } else if (throwable instanceof DataIntegrityViolationException) {
            message = MessageUtils.getMessage("messages", "sale.constraint.error");
            exception = new SaleException(message, throwable, DomainError.CONSTRAINT_ERROR);
        } else if (throwable instanceof DataAccessException) {
            message = MessageUtils.getMessage("messages", "sale.access.error");
            exception = new SaleException(message, throwable, DomainError.IO_ERROR);
        } else {
            message = MessageUtils.getMessage("messages", "error");
            exception = new SaleException(message, throwable, DomainError.ERROR);
        }

        return exception;
    }


    /**
     * Keep in cache offers
     * <p>
     * Since offers don't change often, load them to memory to make computation faster
     * Reload every 12 hours
     */
    @PostConstruct
    @Scheduled(cron = "0 */12 * * *")
    private void retrieveOffers() {
        CashbackOfferRepository repository = SpringContext.getBean(CashbackOfferRepository.class);
        repository.findAll().forEach(offer -> offers.put(offer.getId(), offer.getValues()));
    }
}
