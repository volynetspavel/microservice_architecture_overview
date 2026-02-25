package com.microservice.song.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for sending error responses, including error message and error code.
 */
@Getter
@Setter
public class ErrorResponseDto {
    private String errorMessage;
    private String errorCode;

    public ErrorResponseDto(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
