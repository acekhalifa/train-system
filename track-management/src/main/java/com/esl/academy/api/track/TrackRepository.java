package com.esl.academy.api.track;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {

    Optional<Track> findByTrackIdAndIsDeletedFalse(UUID trackId);

    Page<Track> findByIsDeletedFalse(Pageable pageable);

    Optional<Track> findByNameAndIsDeletedFalse(String name);

}
