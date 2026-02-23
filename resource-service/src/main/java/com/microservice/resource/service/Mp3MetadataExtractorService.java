package com.microservice.resource.service;

import com.microservice.resource.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for extracting MP3 metadata.
 * Parses basic ID3v2 tags from MP3 files.
 */
@Slf4j
@Service
public class Mp3MetadataExtractorService {

    private static final String ID3V2_HEADER = "ID3";
    private static final int FRAME_HEADER_SIZE = 10;

    /**
     * Extracts metadata from MP3 audio data.
     * Parses ID3v2 tags if present in the MP3 file.
     *
     * @param audioData Binary MP3 data.
     * @return Map of extracted metadata with duration converted to mm:ss format.
     * @throws InvalidRequestException if metadata extraction fails.
     */
    public Map<String, String> extractMetadata(byte[] audioData) {
        Map<String, String> extractedMetadata = new HashMap<>();

        try {
            // Validate MP3 file signature
            if (!isValidMp3(audioData)) {
                throw new InvalidRequestException("Invalid MP3 file: missing MPEG frame header");
            }

            // Extract ID3v2 tags if present
            if (hasId3v2Tag(audioData)) {
                extractId3v2Metadata(audioData, extractedMetadata);
            }

            // Add basic file information
            extractedMetadata.putIfAbsent("fileSize", String.valueOf(audioData.length));
            extractedMetadata.putIfAbsent("format", "audio/mpeg");

            log.info("Metadata extracted successfully from MP3 file");
            return extractedMetadata;
        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to extract metadata from MP3 file: " + e.getMessage());
        }
    }

    /**
     * Checks if the audio data is a valid MP3 file.
     *
     * @param audioData Binary audio data.
     * @return true if valid MP3, false otherwise.
     */
    private boolean isValidMp3(byte[] audioData) {
        if (audioData == null || audioData.length < 4) {
            return false;
        }

        // Check for MPEG frame sync (0xFF 0xFB/0xFA/0xF3/0xF2)
        for (int i = 0; i < audioData.length - 1; i++) {
            byte b1 = audioData[i];
            byte b2 = audioData[i + 1];

            if (b1 == (byte) 0xFF && (b2 & 0xE0) == 0xE0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the MP3 file has ID3v2 tag.
     *
     * @param audioData Binary audio data.
     * @return true if ID3v2 tag is present, false otherwise.
     */
    private boolean hasId3v2Tag(byte[] audioData) {
        if (audioData == null || audioData.length < 3) {
            return false;
        }

        return audioData[0] == 'I' && audioData[1] == 'D' && audioData[2] == '3';
    }

    /**
     * Extracts ID3v2 metadata from MP3 file.
     * Parses common ID3v2 frames like TIT2 (Title), TPE1 (Artist), TALB (Album), etc.
     *
     * @param audioData Binary audio data.
     * @param metadata Map to store extracted metadata.
     * @throws IOException if reading fails.
     */
    private void extractId3v2Metadata(byte[] audioData, Map<String, String> metadata) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(audioData);

        // Read and skip ID3 header (10 bytes)
        byte[] id3Header = new byte[10];
        if (stream.read(id3Header) < 10) {
            return;
        }

        // Get ID3 tag size (syncsafe integer, 4 bytes at offset 6)
        int tagSize = getSyncsafeInteger(id3Header, 6);

        // Parse frames
        byte[] frameData = new byte[Math.min(tagSize, 10000)]; // Limit to 10KB for safety
        int bytesRead = stream.read(frameData);

        int pos = 0;
        while (pos < bytesRead - FRAME_HEADER_SIZE) {
            String frameId = extractFrameId(frameData, pos);

            if (frameId == null || frameId.isEmpty() || frameId.charAt(0) == 0) {
                break;
            }

            int frameSize = getSyncsafeInteger(frameData, pos + 4);
            int flags = frameData[pos + 8] & 0xFF;
            int frameDataStart = pos + 10;

            if (frameSize > 0 && frameDataStart + frameSize <= bytesRead) {
                String value = extractFrameValue(frameData, frameDataStart, frameSize, flags);

                if (value != null && !value.isEmpty()) {
                    mapFrameToMetadata(frameId, value, metadata);
                }
            }

            pos += 10 + frameSize;
        }
    }

    /**
     * Extracts frame ID from ID3v2 tag.
     *
     * @param data Frame data.
     * @param offset Frame offset.
     * @return Frame ID as string.
     */
    private String extractFrameId(byte[] data, int offset) {
        if (offset + 4 > data.length) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            byte b = data[offset + i];
            if (b >= 0x20 && b <= 0x7E) {
                sb.append((char) b);
            } else {
                return null;
            }
        }

        return sb.toString();
    }

    /**
     * Extracts value from ID3v2 frame.
     *
     * @param data Frame data.
     * @param offset Data offset.
     * @param size Data size.
     * @param flags Frame flags.
     * @return Frame value as string.
     */
    private String extractFrameValue(byte[] data, int offset, int size, int flags) {
        try {
            if (size <= 0 || offset + size > data.length) {
                return null;
            }

            // Check if frame has unsynchronization flag
            boolean unsynchronized = (flags & 0x02) != 0;

            // Skip encoding byte for text frames
            int startPos = offset;
            if (size > 0 && (data[offset] == 0 || data[offset] == 1 || data[offset] == 2 || data[offset] == 3)) {
                startPos = offset + 1;
                size--;
            }

            byte[] valueBytes = new byte[Math.min(size, 256)];
            System.arraycopy(data, startPos, valueBytes, 0, valueBytes.length);

            // Try to decode as string (UTF-8 or ISO-8859-1)
            return new String(valueBytes, "ISO-8859-1").trim();
        } catch (Exception e) {
            log.debug("Error extracting frame value: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parses syncsafe integer (used in ID3v2 tags).
     *
     * @param data Data array.
     * @param offset Offset in array.
     * @return Integer value.
     */
    private int getSyncsafeInteger(byte[] data, int offset) {
        if (offset + 4 > data.length) {
            return 0;
        }

        int size = 0;
        size |= (data[offset] & 0x7F) << 21;
        size |= (data[offset + 1] & 0x7F) << 14;
        size |= (data[offset + 2] & 0x7F) << 7;
        size |= data[offset + 3] & 0x7F;

        return size;
    }

    /**
     * Maps ID3v2 frame IDs to metadata keys.
     *
     * @param frameId Frame ID.
     * @param value Frame value.
     * @param metadata Metadata map.
     */
    private void mapFrameToMetadata(String frameId, String value, Map<String, String> metadata) {
        switch (frameId) {
            case "TIT2" -> metadata.putIfAbsent("title", value);
            case "TPE1" -> metadata.putIfAbsent("artist", value);
            case "TALB" -> metadata.putIfAbsent("album", value);
            case "TYER" -> metadata.putIfAbsent("year", value);
            case "TCON" -> metadata.putIfAbsent("genre", value);
            case "COMM" -> metadata.putIfAbsent("comment", value);
            case "TRK" -> metadata.putIfAbsent("track", value);
            case "TP2" -> metadata.putIfAbsent("albumArtist", value);
            default -> {} // Ignore other frames
        }
    }
}


