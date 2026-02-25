package com.microservice.song.repository;

import com.microservice.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing song metadata records in the database.
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
}

