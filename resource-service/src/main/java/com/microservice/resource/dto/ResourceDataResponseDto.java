package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for responding with the binary audio data of a resource.
 */
@Getter
@Setter
public class ResourceDataResponseDto {
    private byte[] audioData;

    public ResourceDataResponseDto(byte[] audioData) {
        this.audioData = audioData;
    }
}
