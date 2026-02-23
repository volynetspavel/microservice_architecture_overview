package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for responding with the IDs of deleted resources.
 */
@Getter
@Setter
public class DeleteResourcesResponseDto {
    private List<Long> ids;

    public DeleteResourcesResponseDto(List<Long> ids) {
        this.ids = ids;
    }
}
