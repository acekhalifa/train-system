package com.esl.academy.api.assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    @Query(
        value = """
            SELECT *
            FROM assessment a
            WHERE a.is_deleted = false
              AND a.created_by ->> 'userId' = :userId
        """,
        countQuery = """
            SELECT count(*)
            FROM assessment a
            WHERE a.is_deleted = false
              AND a.created_by ->> 'userId' = :userId
        """,
        nativeQuery = true
    )
    Page<Assessment> findByCreator(@Param("userId") String userId, Pageable pageable);

    @Query(
        value = """
            SELECT a.*
            FROM assessment a
            JOIN learning_resource lr ON a.learning_resource_id = lr.id
            WHERE a.is_deleted = false
              AND lr.track_id = :trackId
              AND a.created_by ->> 'userId' = :userId
        """,
        countQuery = """
            SELECT count(*)
            FROM assessment a
            JOIN learning_resource lr ON a.learning_resource_id = lr.id
            WHERE a.is_deleted = false
              AND lr.track_id = :trackId
              AND a.created_by ->> 'userId' = :userId
        """,
        nativeQuery = true
    )
    Page<Assessment> findByTrackAndCreator(
        @Param("trackId") UUID trackId,
        @Param("userId") String userId,
        Pageable pageable
    );

    @Query(
        value = """
            SELECT count(*)
            FROM assessment a
            JOIN learning_resource lr ON a.learning_resource_id = lr.id
            WHERE lr.track_id = :trackId
        """,
        nativeQuery = true
    )
    int countAssessmentsInTrack(@Param("trackId") UUID trackId);

    List<Assessment> findByLearningResource_Track_TrackId(UUID trackId);
}
