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
    private List<Integer> ids;

    public DeleteResourcesResponseDto(List<Integer> ids) {
        this.ids = ids;
    }
}
