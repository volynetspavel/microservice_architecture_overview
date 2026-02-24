package com.microservice.resource.service;

import com.microservice.resource.dto.DeleteResourcesResponseDto;
import com.microservice.resource.dto.ResourceDataResponseDto;
import com.microservice.resource.dto.ResourceIdResponseDto;
import com.microservice.resource.entity.Resource;
import com.microservice.resource.exception.InvalidRequestException;
import com.microservice.resource.exception.ResourceNotFoundException;
import com.microservice.resource.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for handling MP3 resource CRUD operations.
 */
@Slf4j
@Service
public class ResourceService {

    private final ResourceRepository repository;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    public ResourceService(ResourceRepository repository,
                           Mp3MetadataExtractor metadataExtractor,
                           SongServiceClient songServiceClient) {
        this.repository = repository;
        this.metadataExtractor = metadataExtractor;
        this.songServiceClient = songServiceClient;
    }

    /**
     * Uploads an MP3 file, extracts metadata, and stores it.
     *
     * @param audioData Binary MP3 data.
     * @return DTO containing the ID of the created resource.
     */
    public ResourceIdResponseDto uploadResource(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new InvalidRequestException("MP3 file is empty");
        }

        // Save resource to database
        Resource resource = repository.save(new Resource(audioData));
        log.info("Resource with ID={} saved successfully", resource.getId());

        try {
            // Extract metadata from MP3 file
            Map<String, String> metadata = metadataExtractor.extractMetadata(resource.getId(), audioData);
            log.info("Metadata extracted for resource ID={}", resource.getId());

            // Send metadata to Song Service
            songServiceClient.sendMetadata(metadata);
            log.info("Metadata sent to Song Service for resource ID={}", resource.getId());
        } catch (Exception e) {
            // Log error but don't fail the upload - resource is already saved
            log.error("Error processing metadata for resource ID={}: {}", resource.getId(), e.getMessage());
        }

        return new ResourceIdResponseDto(resource.getId());
    }

    /**
     * Retrieves the audio data for a resource.
     *
     * @param id Resource ID.
     * @return Binary MP3 data.
     */
    public ResourceDataResponseDto getResourceById(String id) {
        int validatedId = validateResourceId(id);
        return repository.findById(validatedId)
                .map(resource -> new ResourceDataResponseDto(resource.getAudioData()))
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"));
    }

    /**
     * Deletes resources by IDs.
     *
     * @param resourceIds Comma-separated string of resource IDs to delete.
     * @return DTO containing the IDs of successfully deleted resources.
     */
    public DeleteResourcesResponseDto deleteResources(String resourceIds) {
        validateCsvLength(resourceIds);
        List<Integer> ids = parseCsvIds(resourceIds);

        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : ids) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                songServiceClient.deleteMetadata(id);
                deletedIds.add(id);
                log.info("Resource with ID={} deleted successfully", id);
            }
        }

        return new DeleteResourcesResponseDto(deletedIds);
    }

    /**
     * Validates if the CSV string length is within acceptable limits.
     *
     * @param resourceIds CSV string of IDs.
     * @throws InvalidRequestException if CSV string is too long.
     */
    private void validateCsvLength(String resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            throw new InvalidRequestException("CSV string cannot be empty");
        }
        if (resourceIds.length() > 200) {
            throw new InvalidRequestException("CSV string is too long: received " + resourceIds.length() + " characters, maximum allowed is 200");
        }
    }

    /**
     * Parses comma-separated string of IDs into a list of Longs.
     *
     * @param resourceIds CSV string of IDs.
     * @return List of parsed IDs.
     * @throws InvalidRequestException if IDs cannot be parsed.
     */
    private List<Integer> parseCsvIds(String resourceIds) {
        List<String> stringIds = Arrays.stream(resourceIds.split(","))
                .map(String::trim)
                .toList();

        try {
            return stringIds.stream()
                    .peek(this::validateCsvFormat)
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid IDs in the provided CSV string");
        }
    }

    /**
     * Validates CSV format.
     *
     * @param resourceIds CSV string of IDs.
     * @throws InvalidRequestException if CSV format is invalid.
     */
    private void validateCsvFormat(String resourceIds) {
        if (!resourceIds.matches("^\\d+(?:,\\s*\\d+)*$")) {
            throw new InvalidRequestException("Invalid ID format: '" + resourceIds + "'. Only positive integers are allowed");
        }
    }

    /**
     * Validates if the provided ID is a positive number.
     *
     * @param id Resource ID to validate.
     * @throws InvalidRequestException if ID is invalid.
     */
    private int validateResourceId(String id) {
        int parsedId;
        try {
            parsedId = Integer.parseInt(id);
            if (parsedId <= 0) {
                throw new InvalidRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
            }
            return parsedId;
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
    }
}