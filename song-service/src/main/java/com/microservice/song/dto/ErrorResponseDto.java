package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for error responses.
 */
@Getter
@Setter
public class ErrorResponseDto {
    private String status;
    private String message;

    public ErrorResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }
}

