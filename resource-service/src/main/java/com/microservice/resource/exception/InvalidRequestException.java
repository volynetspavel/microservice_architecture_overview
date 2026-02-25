package com.microservice.resource.exception;

/**
 * Custom exception for invalid requests in the Resource Service.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}