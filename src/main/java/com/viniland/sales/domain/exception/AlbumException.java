package com.viniland.sales.domain.exception;

import lombok.Getter;

/**
 * Album exception
 */
@Getter
public class AlbumException extends DomainException {

    public AlbumException(String message, DomainError error) {
        super(message, error);
    }

    public AlbumException(String message, Throwable cause, DomainError error) {
        super(message, cause, error);
    }

}
