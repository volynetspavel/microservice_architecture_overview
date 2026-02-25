package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for responding with the ID of a created song metadata record.
 */
@Getter
@Setter
public class SongIdResponseDto {
    private int id;

    public SongIdResponseDto(int id) {
        this.id = id;
    }
}