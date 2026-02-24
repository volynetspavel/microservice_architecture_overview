package com.microservice.song.controller;

import com.microservice.song.dto.DeleteSongsResponseDto;
import com.microservice.song.dto.SongCreateRequestDto;
import com.microservice.song.dto.SongIdResponseDto;
import com.microservice.song.dto.SongResponseDto;
import com.microservice.song.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing song metadata CRUD operations.
 */
@RestController
@RequestMapping("/songs")
public class SongController {
    @Autowired
    private SongService songService;

    /**
     * Creates a new song metadata record.
     *
     * @param requestDto DTO containing song metadata.
     * @return ResponseEntity with the ID of the created song.
     */
    @PostMapping
    public ResponseEntity<SongIdResponseDto> createSong(@RequestBody SongCreateRequestDto requestDto) {
        SongIdResponseDto response = songService.createSong(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Retrieves song metadata by ID.
     *
     * @param id The ID of the song to retrieve.
     * @return ResponseEntity with the song metadata.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDto> getSongById(@PathVariable String id) {
        SongResponseDto response = songService.getSongById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes specified song metadata records by their IDs.
     *
     * @param id Comma-separated list of song IDs to delete.
     * @return ResponseEntity with the IDs of successfully deleted songs.
     */
    @DeleteMapping
    public ResponseEntity<DeleteSongsResponseDto> deleteSongs(@RequestParam String id) {
        DeleteSongsResponseDto response = songService.deleteSongs(id);
        return ResponseEntity.ok(response);
    }
}

