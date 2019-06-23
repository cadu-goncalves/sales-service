package com.viniland.sales.domain.exception;

import lombok.Getter;

/**
 * Sale item exception
 */
@Getter
public class SaleItemException extends DomainException {

    public SaleItemException(String message, DomainError error) {
        super(message, error);
    }

    public SaleItemException(String message, Throwable cause, DomainError error) {
        super(message, cause, error);
    }

}

