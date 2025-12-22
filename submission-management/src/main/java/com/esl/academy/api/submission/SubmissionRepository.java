package com.esl.academy.api.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    boolean existsByIntern_UserIdAndAssessment_AssessmentId(UUID userId, UUID assessmentId);

    List<Submission> findByIntern_UserId(UUID userId);

    Optional<Submission> findByIntern_UserIdAndAssessment_AssessmentId(UUID userId, UUID assessmentId);

    List<Submission> findByAssessment_AssessmentIdIn(List<UUID> assessmentIds);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.intern.userId = :userId AND s.gradingStatus = :gradingStatus")
    long countByInternIdAndGradingStatus(@Param("userId") UUID userId, @Param("gradingStatus") String gradingStatus);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.intern.userId = :userId AND s.submissionStatus = :submissionStatus")
    long countByInternIdAndSubmissionStatus(@Param("userId") UUID userId, @Param("submissionStatus") String submissionStatus);

    @Query(value = """
        SELECT COUNT(a.assessment_id)
        FROM assessment a
        WHERE a.deadline < NOW()
          AND a.published_status = 'PUBLISHED'
          AND a.is_deleted = FALSE
          AND NOT EXISTS (
              SELECT 1
              FROM submission s
              WHERE s.assessment_id = a.assessment_id
                AND s.user_id = :userId
                AND s.is_deleted = FALSE
          )
        """,
        nativeQuery = true)
    long countMissedAssessmentsByInternId(@Param("userId") UUID userId);

    @Query(value = """
        SELECT AVG(s.score)
        FROM submission s
        JOIN intern i ON i.user_id = s.user_id
        WHERE s.grading_status = 'GRADED'
          AND s.is_deleted = FALSE
          AND i.track_id = :trackId
    """,
        nativeQuery = true)
    Double getAverageGradedSubmissionsScoreForTrack(@Param("trackId") UUID trackId);

    @Query(value = """
        SELECT COUNT(s.submission_id)
        FROM submission s
        JOIN intern i
            ON i.user_id = s.user_id
        WHERE i.track_id = :trackId
          AND s.is_deleted = FALSE
          AND s.submission_status = 'SUBMITTED'
          AND s.grading_status == 'NOT_GRADED'
        """,
        nativeQuery = true)
    long countSubmittedNotGradedByTrack(@Param("trackId") UUID trackId);

}
