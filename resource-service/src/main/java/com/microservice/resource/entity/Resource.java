package com.microservice.resource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an MP3 resource stored in the database.
 */
@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "audio_data", nullable = false, columnDefinition = "BYTEA")
    private byte[] audioData;

    public Resource(byte[] audioData) {
        this.audioData = audioData;
    }
}
