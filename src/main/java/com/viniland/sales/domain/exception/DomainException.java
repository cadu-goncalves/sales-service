package com.viniland.sales.domain.exception;

import lombok.Getter;

/**
 * Domain exception
 */
@Getter
public class DomainException extends RuntimeException {

    private DomainError error;

    public DomainException(String message, DomainError error) {
        super(message);
        this.error = error;
    }

    public DomainException(String message, Throwable cause, DomainError error) {
        super(message, cause);
        this.error = error;
    }

}
