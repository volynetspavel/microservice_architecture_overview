package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for error responses.
 */
@Getter
@Setter
public class ErrorResponseDto {
    private String errorCode;
    private String errorMessage;

    public ErrorResponseDto(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

