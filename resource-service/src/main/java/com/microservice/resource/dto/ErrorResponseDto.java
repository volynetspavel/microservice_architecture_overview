package com.microservice.resource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for sending error responses to clients.
 */
@Getter
@Setter
public class ErrorResponseDto {
    private int errorCode;
    private String errorMessage;

    public ErrorResponseDto(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
