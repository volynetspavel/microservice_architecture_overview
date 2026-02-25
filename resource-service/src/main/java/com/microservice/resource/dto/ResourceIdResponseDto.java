package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for responding with the ID of a created resource.
 */
@Getter
@Setter
public class ResourceIdResponseDto {
    private int id;

    public ResourceIdResponseDto(int id) {
        this.id = id;
    }
}
