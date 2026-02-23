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
     * @return true if metadata was successfully sent.
     */
    public boolean sendMetadata(Map<String, String> metadata) {
        try {
            String url = songServiceUrl + "/songs";

            restTemplate.postForObject(url, metadata, Void.class);
            log.info("Metadata successfully sent to Song Service");
            return true;
        } catch (RestClientException e) {
            log.error("Failed to send metadata to Song Service: {}", e.getMessage());
            return false;
        }
    }
}

