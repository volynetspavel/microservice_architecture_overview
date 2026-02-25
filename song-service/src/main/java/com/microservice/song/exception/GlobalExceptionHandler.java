package com.microservice.song.exception;

import com.microservice.song.dto.ErrorResponseDto;
import com.microservice.song.dto.ValidationErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for song service exceptions.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles the case when a requested song is not found in the database.
     */
    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(SongNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage()));
    }

    /**
     * Handles the case when a song with the same ID already exists during creation.
     */
    @ExceptionHandler(SongAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(SongAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(String.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage()));
    }

    /**
     * Handles any unexpected exceptions that occur during request processing.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleServerError(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "Internal server error"));
    }

    /**
     * Handles requests with unsupported Content-Type.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        "Invalid file format: " + ex.getContentType() + ". Only MP3 files are allowed"));
    }

    /**
     * Handles validation errors for method arguments, such as invalid field values in the request body.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> details = new LinkedHashMap<>();

        // Group errors by field
        Map<String, FieldError> otherErrors = new LinkedHashMap<>();
        Map<String, FieldError> notBlankErrors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String code = error.getCode(); // e.g., "NotBlank", "Size", etc.

            if ("Size".equals(code) || "Pattern".equals(code)) {
                otherErrors.put(field, error);
            } else {
                notBlankErrors.put(field, error);
            }
        }

        // Add NotBlank errors first, then other errors
        otherErrors.forEach((field, error) -> details.putIfAbsent(field, error.getDefaultMessage()));
        notBlankErrors.forEach((field, error) -> details.putIfAbsent(field, error.getDefaultMessage()));

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Validation error", details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    /**
     * Handles invalid requests, such as incorrect path variables.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage()));
    }
}

