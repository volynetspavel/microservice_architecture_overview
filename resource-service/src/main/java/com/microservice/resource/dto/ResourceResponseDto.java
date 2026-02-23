package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for responding with the ID of a created resource.
 */
@Getter
@Setter
public class ResourceResponseDto {
    private Long id;

    public ResourceResponseDto(Long id) {
        this.id = id;
    }
}
