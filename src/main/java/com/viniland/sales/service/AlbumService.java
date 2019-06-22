package com.viniland.sales.service;

import com.viniland.sales.domain.exception.AlbumException;
import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.model.Album;
import com.viniland.sales.domain.rest.filter.AlbumFilter;
import com.viniland.sales.persistence.AlbumRepository;
import com.viniland.sales.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@Slf4j
public class AlbumService {

    private final TaskExecutor executor;

    private final AlbumRepository repository;

    public AlbumService(TaskExecutor executor, AlbumRepository repository) {
        this.executor = executor;
        this.repository = repository;
    }

    /**
     * Retrieve album
     *
     * @param id album unique identifier
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Album> retrieve(String id) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Retrieve album: {}", id);

            Optional<Album> findResult = repository.findById(id);
            if (!findResult.isPresent()) {
                // Not found
                String message = MessageUtils.getMessage("messages", "album.notfound");
                throw new AlbumException(message, DomainError.RETRIEVE_ERROR);
            }

            return findResult.get();
        }, executor)
                .exceptionally(throwable -> {
                    log.error(throwable.getMessage());
                    throw translateException(throwable);
                });
    }

    /**
     * Search multiple albuns
     *
     * @param filter {@link AlbumFilter}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<Page<Album>> search(final AlbumFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Search albums: {}", filter);

            // Sort & Pagination
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            PageRequest page = PageRequest.of(filter.getPage(), filter.getSize(), sort);

            // Find
            if (Objects.isNull(filter.getGenre())) {
                return repository.findAll(page);
            } else {
                Album probe = new Album();
                probe.setGenre(filter.getGenre());

                ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("genre", ignoreCase());
                Example<Album> example = Example.of(probe, matcher);
                return repository.findAll(example, page);
            }
        }, executor)
                .exceptionally(throwable -> {
                    log.error(throwable.getMessage());
                    throw translateException(throwable);
                });
    }

    /**
     * Translate relevant exceptions into {@link AlbumException}
     *
     * @param throwable {@link Throwable} original exception
     * @return {@link AlbumException}
     */
    private AlbumException translateException(Throwable throwable) {
        String message;
        AlbumException exception;

        // Unwrap
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        // Check
        if (throwable instanceof AlbumException) {
            // Nothing to do
            return (AlbumException) throwable;
        } else if (throwable instanceof DataIntegrityViolationException) {
            message = MessageUtils.getMessage("messages", "album.constraint.error");
            exception = new AlbumException(message, throwable, DomainError.CONSTRAINT_ERROR);
        } else if (throwable instanceof DataAccessException) {
            message = MessageUtils.getMessage("messages", "album.access.error");
            exception = new AlbumException(message, throwable, DomainError.IO_ERROR);
        } else {
            message = MessageUtils.getMessage("messages", "error");
            exception = new AlbumException(message, throwable, DomainError.ERROR);
        }

        return exception;
    }
}
