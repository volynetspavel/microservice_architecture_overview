package com.microservice.song.exception;

/**
 * Exception thrown when a request is invalid or contains errors.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}

