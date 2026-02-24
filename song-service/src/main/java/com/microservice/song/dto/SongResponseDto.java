package com.microservice.song.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for responding with song metadata details.
 */
@Getter
@Setter
@NoArgsConstructor
public class SongResponseDto {
    private int id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;

    public SongResponseDto(int id, String name, String artist, String album, String duration, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
    }
}

