package com.microservice.song.exception;

/**
 * Exception thrown when a song metadata record is not found.
 */
public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String message) {
        super(message);
    }
}

