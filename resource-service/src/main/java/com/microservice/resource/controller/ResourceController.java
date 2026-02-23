package com.microservice.resource.controller;

import com.microservice.resource.dto.DeleteResourcesResponseDto;
import com.microservice.resource.dto.ResourceDataResponseDto;
import com.microservice.resource.dto.ResourceResponseDto;
import com.microservice.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    /**
     * Uploads a new MP3 resource.
     */
    @PostMapping(consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<ResourceResponseDto> uploadResource(@RequestBody byte[] audioData) {
        ResourceResponseDto resourceId = resourceService.uploadResource(audioData);
        return ResponseEntity.ok(resourceId);
    }

    /**
     * Retrieves the binary audio data of a resource.
     */
    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<ResourceDataResponseDto> getResourceById(@PathVariable Long id) {
        ResourceDataResponseDto data = resourceService.getResourceById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resource_" + id + ".mp3\"")
                .body(data);
    }

    /**
     * Deletes specified resources by their IDs.
     */
    @DeleteMapping
    public ResponseEntity<DeleteResourcesResponseDto> deleteResources(@RequestParam String ids) {
        DeleteResourcesResponseDto deleteResources = resourceService.deleteResources(ids);
        return ResponseEntity.ok().body(deleteResources);
    }
}