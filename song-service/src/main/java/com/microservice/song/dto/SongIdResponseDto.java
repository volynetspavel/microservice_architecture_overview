package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for responding with the ID of a created song metadata record.
 */
@Getter
@Setter
public class SongIdResponseDto {
    private Long id;

    public SongIdResponseDto(Long id) {
        this.id = id;
    }
}