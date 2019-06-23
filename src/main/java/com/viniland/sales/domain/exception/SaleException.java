package com.viniland.sales.domain.exception;

import lombok.Getter;

/**
 * Sale exception
 */
@Getter
public class SaleException extends DomainException {

    public SaleException(String message, DomainError error) {
        super(message, error);
    }

    public SaleException(String message, Throwable cause, DomainError error) {
        super(message, cause, error);
    }

}
