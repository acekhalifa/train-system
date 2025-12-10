package com.esl.academy.api.intern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternRepository extends JpaRepository<Intern, UUID>, JpaSpecificationExecutor<Intern> {

    Optional<Intern> findByUserIdAndInternStatus(UUID userId, InternStatus internStatus);

    Page<Intern> findByInternStatus(InternStatus internStatus, Pageable pageable);

    Page<Intern> findByTrackId(UUID trackId, Pageable pageable);

}
