package com.esl.academy.api.track;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {

    Optional<Track> findByTrackIdAndIsDeletedFalse(UUID trackId);

    List<Track> findByIsDeletedFalse();

    List<Track> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}
