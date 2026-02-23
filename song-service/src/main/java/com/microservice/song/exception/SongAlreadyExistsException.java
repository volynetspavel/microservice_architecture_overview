package com.microservice.song.exception;

/**
 * Exception thrown when attempting to create a song with an ID that already exists.
 */
public class SongAlreadyExistsException extends RuntimeException {
    public SongAlreadyExistsException(String message) {
        super(message);
    }
}

