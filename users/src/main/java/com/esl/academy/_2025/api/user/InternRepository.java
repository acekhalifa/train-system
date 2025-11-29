package com.esl.academy._2025.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InternRepository extends JpaRepository<Intern, UUID> {

    List<Intern> findByTrackId(UUID trackId);

    boolean existsByTrackId(UUID trackId);
}
