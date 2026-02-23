package com.microservice.song.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Song metadata record in the database.
 */
@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
public class Song {
    @Id
    private Long id;

    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;

    public Song(Long id, String name, String artist, String album, String duration, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }
}

