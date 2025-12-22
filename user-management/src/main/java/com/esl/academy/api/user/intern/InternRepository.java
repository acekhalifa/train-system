package com.esl.academy.api.user.intern;

import com.esl.academy.api.user.Intern;
import com.esl.academy.api.user.InternStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InternRepository extends JpaRepository<Intern, UUID>, JpaSpecificationExecutor<Intern> {

    Page<Intern> findByInternStatus(InternStatus internStatus, Pageable pageable);

    Page<Intern> findByTrack_TrackIdOrderByUser_FirstNameAsc(@NonNull UUID trackId, Pageable pageable);
}
