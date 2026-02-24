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
import java.util.regex.Pattern;

/**
 * Service for handling song metadata CRUD operations.
 */
@Service
public class SongService {

    private final SongRepository repository;
    private static final Pattern DURATION_PATTERN = Pattern.compile("^([0-5]\\d):([0-5]\\d)$");
    private static final int MAX_CSV_LENGTH = 200;

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
        validateSongCreationRequest(requestDto);

        // Check if song with this ID already exists
        if (repository.existsById(requestDto.getId())) {
            throw new SongAlreadyExistsException("Metadata for this ID already exists");
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
    public SongResponseDto getSongById(Long id) {
        validateId(id);

        Optional<Song> song = repository.findById(id);
        if (song.isEmpty()) {
            throw new SongNotFoundException("Resource with ID=" + id + " not found");
        }

        Song s = song.get();
        return new SongResponseDto(s.getId(), s.getName(), s.getArtist(), s.getAlbum(), s.getDuration(), s.getYear());
    }

    /**
     * Deletes specified song metadata records by their IDs.
     *
     * @param ids Comma-separated list of song IDs to delete.
     * @return DTO containing the IDs of successfully deleted songs.
     */
    public DeleteSongsResponseDto deleteSongs(String ids) {
        validateCsvFormat(ids);

        List<Long> idList = parseAndValidateIds(ids);
        List<Long> deletedIds = new ArrayList<>();

        for (Long id : idList) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                deletedIds.add(id);
            }
        }

        return new DeleteSongsResponseDto(deletedIds);
    }

    /**
     * Validates the song creation request.
     *
     * @param requestDto DTO containing song metadata.
     */
    private void validateSongCreationRequest(SongCreateRequestDto requestDto) {
        if (requestDto == null) {
            throw new InvalidRequestException("Song metadata is missing");
        }

        if (requestDto.getId() == null || requestDto.getId() <= 0) {
            throw new InvalidRequestException("Invalid id=" + requestDto.getId() + ": Numeric, must match an existing Resource ID");
        }

        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty() ||
                requestDto.getName().isEmpty() || requestDto.getName().length() > 100) {
            throw new InvalidRequestException("Invalid name='" + requestDto.getName() + "': 1-100 characters text");
        }

        if (requestDto.getArtist() == null || requestDto.getArtist().trim().isEmpty() ||
                requestDto.getArtist().isEmpty() || requestDto.getArtist().length() > 100) {
            throw new InvalidRequestException("Invalid artist='" + requestDto.getArtist() + "': 1-100 characters text");
        }

        if (requestDto.getAlbum() == null || requestDto.getAlbum().trim().isEmpty() ||
                requestDto.getAlbum().isEmpty() || requestDto.getAlbum().length() > 100) {
            throw new InvalidRequestException("Invalid album='" + requestDto.getAlbum() + "': 1-100 characters text");
        }

        if (requestDto.getDuration() == null || !DURATION_PATTERN.matcher(requestDto.getDuration()).matches()) {
            throw new InvalidRequestException("Invalid duration='" + requestDto.getDuration() + "': Format mm:ss, with leading zeros");
        }

        if (requestDto.getYear() == null || !isValidYear(requestDto.getYear())) {
            throw new InvalidRequestException("Invalid year='" + requestDto.getYear() + "': YYYY format between 1900-2099");
        }
    }

    /**
     * Validates a single ID.
     *
     * @param id The ID to validate.
     */
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("The provided ID is invalid (e.g., contains letters, decimals, is negative, or zero)");
        }
    }

    /**
     * Validates the CSV format and length.
     *
     * @param ids Comma-separated list of IDs.
     */
    private void validateCsvFormat(String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            throw new InvalidRequestException("CSV string format is invalid or exceeds length restrictions");
        }

        if (ids.length() > MAX_CSV_LENGTH) {
            throw new InvalidRequestException("CSV string format is invalid or exceeds length restrictions");
        }
    }

    /**
     * Parses and validates comma-separated IDs.
     *
     * @param ids Comma-separated list of IDs.
     * @return List of parsed IDs.
     */
    private List<Long> parseAndValidateIds(String ids) {
        List<Long> idList = new ArrayList<>();
        String[] idStrings = ids.split(",");

        for (String idStr : idStrings) {
            try {
                Long id = Long.parseLong(idStr.trim());
                if (id <= 0) {
                    throw new InvalidRequestException("CSV string format is invalid or exceeds length restrictions");
                }
                idList.add(id);
            } catch (NumberFormatException e) {
                throw new InvalidRequestException("CSV string format is invalid or exceeds length restrictions");
            }
        }

        return idList;
    }

    /**
     * Validates if the year is in YYYY format between 1900-2099.
     *
     * @param year The year string to validate.
     * @return true if valid, false otherwise.
     */
    private boolean isValidYear(String year) {
        if (year == null || !year.matches("^[12]\\d{3}$")) {
            return false;
        }

        int yearInt = Integer.parseInt(year);
        return yearInt >= 1900 && yearInt <= 2099;
    }
}

