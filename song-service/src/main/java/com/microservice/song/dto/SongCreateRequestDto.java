package com.microservice.song.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for receiving song metadata creation requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongCreateRequestDto {
    private int id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}

