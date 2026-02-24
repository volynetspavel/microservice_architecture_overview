package com.microservice.resource.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Client for communicating with Song Service to save MP3 metadata.
 */
@Slf4j
@Service
public class SongServiceClient {

    private final RestTemplate restTemplate;

    @Value("${song-service.url}")
    private String songServiceUrl;

    public SongServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends extracted MP3 metadata to Song Service.
     *
     * @param metadata Map of extracted metadata.
     */
    public void sendMetadata(Map<String, String> metadata) {
        try {
            String url = songServiceUrl + "/songs";

            restTemplate.postForObject(url, metadata, Void.class);
            log.info("Metadata successfully sent to Song Service");
        } catch (RestClientException e) {
            log.error("Failed to send metadata to Song Service: {}", e.getMessage());
        }
    }

    /**
     * Deletes metadata from Song Service by resource ID.
     * @param id The ID of the metadata to delete.
     */
    public void deleteMetadata(Long id) {
        try {
                String url = songServiceUrl + "/songs?id=" + id;

                restTemplate.delete(url);
                log.info("Metadata with ID={} successfully deleted from Song Service", id);
            } catch (RestClientException e) {
                log.error("Failed to delete metadata from Song Service: {}", e.getMessage());
            }
    }
}

