package com.viniland.sales.util.rest;

import com.viniland.sales.domain.exception.DomainError;
import com.viniland.sales.domain.exception.DomainException;
import com.viniland.sales.domain.rest.ResourceError;
import com.viniland.sales.util.MessageUtils;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletionException;

/**
 * Resource error utils
 */
@UtilityClass
public class ResourceErrorBuilder {

    /**
     * Build API error
     *
     * @param throwable {@link Throwable} exception
     * @return {@link ResourceError}
     */
    public static ResourceError build(Throwable throwable) {
        ResourceError error = new ResourceError();

        // Pick cause
        if(isCauseDomainException(throwable)) {
            DomainException exception = (DomainException) throwable.getCause();
            error.addMessage(exception.getMessage());
            error.setStatus(translateError(exception.getError()));
        } else {
            error.addMessage(MessageUtils.getMessage("messages", "error"));
            error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return error;
    }

    /**
     * Translate error to HTTP status (common cases)
     *
     * @param code {@link ResourceError}
     * @return {@link HttpStatus}
     */
    public static HttpStatus translateError(DomainError code) {
        switch (code) {
            case CREATE_ERROR:
            case UPDATE_ERROR:
            case CONSTRAINT_ERROR:
                return  HttpStatus.BAD_REQUEST;

            case RETRIEVE_ERROR:
            case DELETE_ERROR:
                return  HttpStatus.NOT_FOUND;

            case IO_ERROR:
            case ERROR:
                return  HttpStatus.INTERNAL_SERVER_ERROR;

            default: // dymmy
                return  HttpStatus.NOT_FOUND;
        }
    }

    /**
     * Check if cause is {@link DomainException}
     *
     * @param throwable {@link Throwable} exception
     * @return true if match {@link DomainException}, false otherwise
     */
    private static Boolean isCauseDomainException(Throwable throwable) {
        // Unwrap
        if(throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        // Check
        return throwable instanceof DomainException;
    }

}
