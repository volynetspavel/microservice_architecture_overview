package com.microservice.song.exception;

import com.microservice.song.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for song service exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the case when a requested song is not found in the database.
     */
    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(SongNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    /**
     * Handles invalid request exceptions, such as missing required fields or invalid data formats.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Handles the case when a song with the same ID already exists during creation.
     */
    @ExceptionHandler(SongAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(SongAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /**
     * Handles any unexpected exceptions that occur during request processing.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    /**
     * Handles requests with unsupported Content-Type.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(),
                        "Invalid file format: " + ex.getContentType() + ". Only MP3 files are allowed"));
    }

    /**
     * Handles path variable type mismatch errors.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String numberSign = requiredType.equals("Long") ? "positive " : "";
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format(
                "Invalid value '%s' for '%s'. Must be a %s%s.",
                value, paramName, numberSign, requiredType
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(),
                message));
    }
}

