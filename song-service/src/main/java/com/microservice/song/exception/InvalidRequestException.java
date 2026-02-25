package com.microservice.song.exception;

import lombok.Getter;

/**
 * Exception thrown when a request is invalid or contains errors.
 */
@Getter
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}

