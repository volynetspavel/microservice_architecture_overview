package com.microservice.resource.service;

import com.microservice.resource.dto.DeleteResourcesResponseDto;
import com.microservice.resource.dto.ResourceDataResponseDto;
import com.microservice.resource.dto.ResourceResponseDto;
import com.microservice.resource.entity.Resource;
import com.microservice.resource.exception.InvalidRequestException;
import com.microservice.resource.exception.ResourceNotFoundException;
import com.microservice.resource.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for handling MP3 resource CRUD operations.
 */
@Service
public class ResourceService {

    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    /**
     * Uploads an MP3 file, extracts metadata, and stores it.
     *
     * @param audioData Binary MP3 data.
     * @return DTO containing the ID of the created resource.
     */
    public ResourceResponseDto uploadResource(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new InvalidRequestException("MP3 file is empty");
        }

        Resource resource = repository.save(new Resource(audioData));

//        Mp3Metadata metadata = extractMetadata(data);
//        songServiceClient.saveMetadata(metadata);

        return new ResourceResponseDto(resource.getId());
    }

    /**
     * Retrieves the audio data for a resource.
     *
     * @param id Resource ID.
     * @return Binary MP3 data.
     */
    public ResourceDataResponseDto getResourceById(Long id) {
        validateResourceId(id);
        return repository.findById(id)
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
        if (resourceIds.length() > 200) throw new InvalidRequestException("CSV string too long");

        List<Long> ids;
        try {
            ids = Arrays.stream(resourceIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid ids in the provided CSV string");
        }

        List<Long> deletedIds = new ArrayList<>();
        for (Long id : ids) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                deletedIds.add(id);
            }
        }

        return new DeleteResourcesResponseDto(deletedIds);
    }

    /**
     * Validates if the provided ID is a positive number.
     *
     * @param id Resource ID to validate.
     * @throws InvalidRequestException if ID is invalid.
     */
    private void validateResourceId(Long id) {
        if (id == null || id <= 0 || !String.valueOf(id).matches("^\\d+$")) {
            throw new InvalidRequestException("Invalid resource ID: " + id);
        }
    }
}