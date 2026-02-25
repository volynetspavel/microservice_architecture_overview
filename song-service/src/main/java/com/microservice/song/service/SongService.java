package com.microservice.song.service;

import com.microservice.song.dto.DeleteSongsResponseDto;
import com.microservice.song.dto.SongCreateRequestDto;
import com.microservice.song.dto.SongIdResponseDto;
import com.microservice.song.dto.SongResponseDto;
import com.microservice.song.entity.Song;
import com.microservice.song.exception.InvalidRequestException;
import com.microservice.song.exception.SongAlreadyExistsException;
import com.microservice.song.exception.SongNotFoundException;
import com.microservice.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling song metadata CRUD operations.
 */
@Service
public class SongService {

    private final SongRepository repository;

    public SongService(SongRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new song metadata record.
     *
     * @param requestDto DTO containing song metadata.
     * @return DTO containing the ID of the created song.
     */
    public SongIdResponseDto createSong(SongCreateRequestDto requestDto) {
        // Check if song with this ID already exists
        if (repository.existsById(requestDto.getId())) {
            throw new SongAlreadyExistsException("Metadata for resource ID=" + requestDto.getId() + " already exists");
        }

        Song song = new Song(
                requestDto.getId(),
                requestDto.getName(),
                requestDto.getArtist(),
                requestDto.getAlbum(),
                requestDto.getDuration(),
                requestDto.getYear()
        );

        Song savedSong = repository.save(song);
        return new SongIdResponseDto(savedSong.getId());
    }

    /**
     * Retrieves a song metadata by ID.
     *
     * @param id The ID of the song.
     * @return DTO containing the song metadata.
     */
    public SongResponseDto getSongById(String id) {
        int validatedId = validateId(id);

        Optional<Song> song = repository.findById(validatedId);
        if (song.isEmpty()) {
            throw new SongNotFoundException("Song metadata for ID=" + id + " not found");
        }

        Song s = song.get();
        return new SongResponseDto(s.getId(), s.getName(), s.getArtist(), s.getAlbum(), s.getDuration(), s.getYear());
    }

    /**
     * Deletes specified song metadata records by their IDs.
     *
     * @param songIds Comma-separated list of song IDs to delete.
     * @return DTO containing the IDs of successfully deleted songs.
     */
    public DeleteSongsResponseDto deleteSongs(String songIds) {
        validateCsvLength(songIds);
        List<Integer> ids = parseCsvIds(songIds);

        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : ids) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                deletedIds.add(id);
            }
        }

        return new DeleteSongsResponseDto(deletedIds);
    }

    /**
     * Validates a single ID.
     *
     * @param id The ID to validate.
     */
    private int validateId(String id) {
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
}

