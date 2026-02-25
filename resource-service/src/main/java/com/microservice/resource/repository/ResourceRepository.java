package com.microservice.resource.repository;

import com.microservice.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing MP3 resources in the database.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
}