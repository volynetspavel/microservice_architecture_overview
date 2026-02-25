package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * DTO for sending validation error responses, including error message, error code, and field-specific details.
 */
@Getter
@Setter
public class ValidationErrorResponseDto {
    private String errorMessage;
    private Map<String, String> details;
    private String errorCode;

    public ValidationErrorResponseDto(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ValidationErrorResponseDto(String errorCode, String errorMessage, Map<String, String> details) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;
    }
}

