package com.microservice.resource.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an MP3 resource stored in the database.
 */
@Getter
@Setter
public class Resource {
    private Long id;
    private byte[] audioData;

    public Resource(byte[] audioData) {
        this.audioData = audioData;
    }
}
