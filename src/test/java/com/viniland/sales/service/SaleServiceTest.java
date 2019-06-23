package com.viniland.sales.service;

import com.google.common.collect.Lists;
import com.viniland.sales.domain.exception.SaleException;
import com.viniland.sales.domain.exception.SaleItemException;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.model.CashbackOffer;
import com.viniland.sales.domain.model.Sale;
import com.viniland.sales.domain.rest.SaleResource;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.domain.rest.filter.SaleFilter;
import com.viniland.sales.persistence.AlbumRepository;
import com.viniland.sales.persistence.CashbackOfferRepository;
import com.viniland.sales.persistence.SaleRepository;
import com.viniland.sales.util.WeekDayUtils;
import com.viniland.sales.util.rest.ResourceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SaleService}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SaleServiceTest {

    @MockBean
    private SaleRepository mockRepo;

    @MockBean
    private AlbumRepository mockAlbumRepo;

    @MockBean
    private CashbackOfferRepository mockOfferRepo;

    @Autowired
    private SaleService service;

    private Sale sale;

    private SaleResource saleResource;

    @Before
    public void beforeEach() {
        // Calendar instance from UTC fails, set timezone first
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date nowUtc = Calendar.getInstance().getTime();

        // Sale fixture
        sale = Sale.builder()
                .id("1")
                .customerId(30L)
                .register(nowUtc)
                .item(new Sale.SaleItem("xxx", 12.1D, 3.1D, 1))
                .item(new Sale.SaleItem("bbb", 22.12D, 4.2D, 3))
                .build();

        // Reset mocks
        reset(mockRepo);
    }

    /**
     * Test scenario for sale retrieve
     *
     * @throws Exception
     */
    @Test
    public void itRetrieveSale() throws Exception {
        //  Mock behaviours
        when(mockRepo.findById(eq("aaa"))).thenReturn(Optional.of(sale));

        // Test
        Sale resultA = service.retrieve("aaa").get();
        assertThat(resultA, equalTo(sale));

        // Check mock iteration
        verify(mockRepo).findById(eq("aaa"));
    }

    /**
     * Test scenario for sale retrieve not found
     *
     * @throws Exception
     */
    @Test
    public void itThrowsSaleNotFound() throws Exception {
        //  Mock behaviours
        when(mockRepo.findById(eq("bbb"))).thenReturn(Optional.empty());

        // Test (must throw exception)
        try {
            service.retrieve("bbb").get();
            Assert.fail();
        } catch (Exception e) {
            if (!(e.getCause() instanceof SaleException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario for sale search
     *
     * @throws Exception
     */
    @Test
    public void itSearchesSales() throws Exception {
        // Input fixtures
        SaleFilter filter = new SaleFilter();
        Sort sort = Sort.by(Sort.Direction.ASC, "register");
        PageRequest reqPage = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Output fixtures
        List<Sale> content = new ArrayList<>();
        content.add(sale);
        Page page = new PageImpl(content, reqPage, 1);

        //  Mock behaviours
        when(mockRepo.findAll(eq(reqPage))).thenReturn(page);

        // Test
        Page<Sale> result = service.search(filter).get();
        assertThat(result.getContent(), hasItem(sale));
        assertThat(result.getNumber(), equalTo(filter.getPage()));
        assertThat(result.getTotalPages(), equalTo(page.getTotalPages()));

        // Check mock iteration
        verify(mockRepo).findAll(eq(reqPage));
    }


    /**
     * Test scenario for sale search
     *
     * @throws Exception
     */
    @Test
    public void itSearchesSalesFiltering() throws Exception {
        // Input fixtures
        SaleFilter filter = new SaleFilter();
        filter.setFrom(new Date());
        filter.setTo(new Date());
        Sort sort = Sort.by(Sort.Direction.ASC, "register");
        PageRequest reqPage = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Output fixtures
        List<Sale> content = new ArrayList<>();
        content.add(sale);
        Page page = new PageImpl(content, reqPage, 1);

        //  Mock behaviours
        when(mockRepo.findByRegisterBetween(any(Date.class), any(Date.class), eq(reqPage))).thenReturn(page);

        // Test
        Page<Sale> result = service.search(filter).get();
        assertThat(result.getContent(), hasItem(sale));
        assertThat(result.getNumber(), equalTo(filter.getPage()));
        assertThat(result.getTotalPages(), equalTo(page.getTotalPages()));

        // Check mock iteration
        verify(mockRepo).findByRegisterBetween(any(Date.class), any(Date.class), eq(reqPage));
    }

    /**
     * Test scenario for sale register with invalid items
     *
     * @throws Exception
     */
    @Test
    public void itThrowsSaleInvalidItems() throws Exception {
        // Input fixtures
        saleResource = ResourceBuilder.asResource(sale, SaleResource.class);

        // Mock behaviours (find just one album)
        Album found = Album.builder().id("xxx").build();
        when(mockAlbumRepo.findAllById(any(Iterable.class))).thenReturn(Lists.newArrayList(found));

        // Test (must throw exception)
        try {
            service.register(saleResource).get();
            Assert.fail();
        } catch (Exception e) {
            if (!(e.getCause() instanceof SaleItemException)) {
                Assert.fail();
            }
        }
    }

    /**
     * Test scenario for sale register
     *
     * @throws Exception
     */
    @Test
    public void itRegisterSale() throws Exception {
        // Input fixtures
        saleResource = ResourceBuilder.asResource(sale, SaleResource.class);
        CashbackOffer.WeekDay weekDay = WeekDayUtils.fromDate(saleResource.getRegister());

        // Offers recovery
        CashbackOffer offer = CashbackOffer.builder().id(weekDay).value("rock", 5d).value("pop", 10d).build();
        when(mockOfferRepo.findAll()).thenReturn(Lists.newArrayList(offer));

        // Albums recovery
        Album albumA = Album.builder().id("xxx").genre("rock").price(20.2).build();
        Album albumB = Album.builder().id("bbb").genre("pop").price(10.1).build();
        when(mockAlbumRepo.findAllById(any(Iterable.class))).thenReturn(Lists.newArrayList(albumA, albumB));

        // Save sale
        when(mockRepo.save(any(Sale.class))).thenAnswer(i -> i.getArguments()[0]);

        // Sale values
        Double purchase = albumA.getPrice() + (albumB.getPrice() * 3);
        Double cashback = (albumA.getPrice() * 5d) / 100 + (albumB.getPrice() * 3 * 10d) / 100;

        // Test
        Sale result = service.register(saleResource).get();
        assertThat(result.getTotalPurchase(), equalTo(purchase));
        assertThat(result.getTotalCashback(), equalTo(cashback));
    }
}
