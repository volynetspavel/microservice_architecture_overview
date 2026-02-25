package com.microservice.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * DTO for MP3 metadata extracted from the audio file.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mp3MetadataDto {
    private Map<String, String> metadata;
}

