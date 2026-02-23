package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for responding with the IDs of deleted songs.
 */
@Getter
@Setter
public class DeleteSongsResponseDto {
    private List<Long> ids;

    public DeleteSongsResponseDto(List<Long> ids) {
        this.ids = ids;
    }
}
