package com.microservice.song.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for receiving song metadata creation requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongCreateRequestDto {
    @Positive(message = "ID must be a positive integer")
    private int id;

    @NotEmpty(message = "Song name is required")
    @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 characters")
    private String name;

    @NotEmpty(message = "Artist name is required")
    @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
    private String artist;

    @NotEmpty(message = "Album name is required")
    @Size(min = 1, max = 100, message = "Album name must be between 1 and 100 characters")
    private String album;

    @NotEmpty(message = "Duration is required")
    @Pattern(regexp = "^\\d{2}:[0-5]\\d$", message = "Duration must be in mm:ss format with leading zeros")
    private String duration;

    @NotEmpty(message = "Year is required")
    @Pattern(regexp = "^\\d{4}$", message = "Year must be between 1900 and 2099")
    private String year;
}

