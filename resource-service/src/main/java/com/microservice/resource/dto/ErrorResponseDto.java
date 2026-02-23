package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for sending error responses to clients.
 */
@Getter
@Setter
public class ErrorResponseDto {
    private String errorCode;
    private String message;

    public ErrorResponseDto(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
