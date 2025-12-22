package com.esl.academy.api.integration.tests.assessment;

import com.esl.academy.api.assessment.AssessmentDto;
import com.esl.academy.api.assessment.AssessmentDto.UpdateAssessmentDto;
import com.esl.academy.api.assessment.AssessmentRepository;
import com.esl.academy.api.assessment.AssessmentService;
import com.esl.academy.api.assessment.PublishedStatus;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.assessment.AssessmentDto.AddAssessmentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssessmentServiceTest extends BaseIntegrationTest {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    private UUID existingAssessmentId;
    private UUID existingLearningResourceId;
    private String mockUserId;
    private UUID existingTrackId;

    @BeforeEach
    void setup() {
        existingAssessmentId = UUID.fromString("b4f46104-dab7-420d-9e22-f9285d192974");
        existingLearningResourceId = UUID.fromString("b4c46104-dac7-430d-9e22-e8285d192974");
        mockUserId = "11111111-1111-1111-1111-111111111111";
        existingTrackId = UUID.fromString("c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12");
    }

    @Test
    void addAssessment_withValidData_shouldAdd() {
        String description = "Some description for the assessment";
        OffsetDateTime deadline = OffsetDateTime.parse("2025-01-15T14:30:00+01:00");
        PublishedStatus publishedStatus = PublishedStatus.PUBLISHED;

        AddAssessmentDto dto = new AddAssessmentDto(
            existingLearningResourceId,
            description,
            deadline,
            publishedStatus
        );

        AssessmentDto saved = assessmentService.addAssessment(dto);

        assertNotNull(saved);
        assertNotNull(saved.assessmentId());
        assertEquals("Some description for the assessment", saved.description());
        assertEquals(OffsetDateTime.parse("2025-01-15T14:30:00+01:00"), saved.deadline());
        assertEquals(PublishedStatus.PUBLISHED, saved.publishedStatus());
    }

    @Test
    void updateAssessment_withValidAssessmentId_shouldUpdate() {

        UpdateAssessmentDto dto = new UpdateAssessmentDto(
            "Updated description",
            null,
            null
        );

        AssessmentDto updated = assessmentService.updateAssessment(existingAssessmentId, dto);

        assertEquals("Updated description", updated.description());
        assertNotNull(updated.publishedStatus());
    }

    @Test
    void updateAssessment_withInvalidAssessmentId_shouldUpdate() {
        UUID invalidId = UUID.randomUUID();
        UpdateAssessmentDto dto = new UpdateAssessmentDto(
            "Updated description",
            null,
            null
        );

        assertThrows(NotFoundException.class, () ->
            assessmentService.updateAssessment(invalidId, dto));
    }

    @Test
    void getAssessmentById_withValidId_shouldGet() {
        AssessmentDto dto = assessmentService.getAssessmentById(existingAssessmentId);

        assertNotNull(dto);
        assertEquals(existingLearningResourceId, dto.learningResourceId());
        assertEquals("An assessment description", dto.description());
    }

    @Test
    @DisplayName("Should fetch all assessments created by logged-in user when no trackId is provided")
    void getAllAssessments_withoutTrackId_returnsUsersAssessments() {

        PageRequest page = PageRequest.of(0, 10);

        Page<AssessmentDto> result =
            assessmentService.getAllAssessments(null, page);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).allSatisfy(dto -> {
            assertEquals(dto.trackId(), existingTrackId);
            assertThat(dto.createdBy()).contains(mockUserId);
        });
    }

    /*
    @Test
    @DisplayName("Should fetch assessments created by user filtered by specific trackId")
    void getAllAssessments_withTrackId_filtersByTrack() {

        UUID trackId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        PageRequest page = PageRequest.of(0, 10);

        Page<AssessmentDto> result =
            assessmentService.getAllAssessments(trackId, page);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).allSatisfy(dto -> {
            assertThat(dto.trackId()).isEqualTo(trackId);
            assertThat(dto.createdBy()).contains(mockUserId);
        });
    }

    @Test
    @DisplayName("Should return empty page if user has no assessments")
    void getAllAssessments_emptyResults() {
        PageRequest page = PageRequest.of(0, 10);

        // replace the mock userId in DB so nothing matches
        // temporarily modify service if needed; or in test DB have no rows for this user

        Page<AssessmentDto> result =
            assessmentService.getAllAssessments(null, page);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
     */
}
