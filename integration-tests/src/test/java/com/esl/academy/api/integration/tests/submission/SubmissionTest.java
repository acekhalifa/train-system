package com.esl.academy.api.integration.tests.submission;

import com.esl.academy.api.assessment.Assessment;
import com.esl.academy.api.assessment.PublishedStatus;
import com.esl.academy.api.core.exceptions.AuthorizationException;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.ConflictException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.learning_resource.LearningResource;
import com.esl.academy.api.submission.GradingStatus;
import com.esl.academy.api.submission.Submission;
import com.esl.academy.api.submission.SubmissionDto;
import com.esl.academy.api.submission.SubmissionRepository;
import com.esl.academy.api.submission.SubmissionService;
import com.esl.academy.api.submission.SubmissionStatus;
import com.esl.academy.api.track.Track;
import com.esl.academy.api.user.Intern;
import com.esl.academy.api.user.InternStatus;
import com.esl.academy.api.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubmissionTest extends BaseIntegrationTest {


    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TestData testData;

    @Autowired
    private SubmissionRepository submissionRepository;

    User supervisor;
    Intern intern;
    Assessment assessment;
    Submission submission;
    LearningResource learningResource;
    Track track;

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "submission.txt",
        "text/plain",
        "content".getBytes()
    );

    @BeforeEach
    void setup() {
        track = testData.createTrack("FRONT END", UUID.randomUUID());

        learningResource = testData.createLearningResource(
            UUID.randomUUID(),
            track,
            "Responsive Design"
        );

        assessment = testData.createAssessment(
            learningResource,
            PublishedStatus.PUBLISHED,
            OffsetDateTime.now().plusDays(2),
            false
        );

        supervisor = testData.createSupervisor(
            UUID.randomUUID(),
            "yakubu@tcmp.com",
            "yakubu",
            "sheme",
            "password",
            track.getTrackId()
        );

        intern = testData.createIntern(
            UUID.randomUUID(),
            "uthman@tcmp.com",
            "uthman",
            "yahaya",
            "password",
            track,
            InternStatus.ACTIVE
        );

        submission = testData.createSubmission(
            intern,
            assessment,
            SubmissionStatus.SUBMITTED,
            GradingStatus.NOT_GRADED,
            "The assessment was engaging",
            0,
            false);


    }

    @Test
    void submitAssessment_shouldSaveSubmissionWithDocumentsAndLinks() throws Exception {
        setAuthenticatedUser(intern.getUser().getEmail());

        var dto = new SubmissionDto.SubmissionRequestDto(
            "My note",
            List.of("https://github.com/intern/submission")
        );

        var response = submissionService.submitAssessment(dto, List.of(file), assessment.getAssessmentId());
        forceFlush();

        var saved = submissionRepository.findByIntern_UserIdAndAssessment_AssessmentId(
            intern.getUserId(),
            assessment.getAssessmentId()
        ).orElseThrow();

        assertThat(saved.getSubmissionStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(saved.getGradingStatus()).isEqualTo(GradingStatus.NOT_GRADED);
        assertThat(saved.getSubmissionNote()).isEqualTo(dto.submissionNote());
        assertThat(saved.getDocuments()).hasSize(1);
        assertThat(response.submissionLinks()).contains("https://github.com/intern/submission");
    }

    @Test
    void submitAssessment_inactiveIntern_shouldThrowException() {
        intern.setInternStatus(InternStatus.DISCONTINUED);
        setAuthenticatedUser(intern.getUser().getEmail());

        var dto = new SubmissionDto.SubmissionRequestDto("note", List.of("link"));

        assertThatThrownBy(() -> submissionService.submitAssessment(dto, List.of(file), assessment.getAssessmentId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Intern is not active");
    }

    @Test
    void submitAssessment_duplicateSubmission_shouldThrowConflictException() throws Exception {
        setAuthenticatedUser(intern.getUser().getEmail());

        // First submission
        var dto = new SubmissionDto.SubmissionRequestDto("note", List.of("link1"));
        submissionService.submitAssessment(dto, List.of(file), assessment.getAssessmentId());

        // Duplicate submission attempt
        var secondFile = new MockMultipartFile("file2", "second.txt", "text/plain", "content2".getBytes());
        var secondDto = new SubmissionDto.SubmissionRequestDto("note2", List.of("link2"));

        assertThatThrownBy(() -> submissionService.submitAssessment(secondDto, List.of(secondFile), assessment.getAssessmentId()))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Assessment already submitted");
    }

    @Test
    void submitAssessment_deadlinePassed_should_throwException() {
        setAuthenticatedUser(intern.getUser().getEmail());
        var dto = new SubmissionDto.SubmissionRequestDto("note", List.of("link1"));
        assertThrows(
            BadRequestException.class,
            () -> submissionService.submitAssessment(
                dto,
                List.of(file),
                assessment.getAssessmentId()
            )
        );
    }

    @Test
    void submitAssessment_trackMismatch_throwsException() {
        setAuthenticatedUser(intern.getUser().getEmail());
        var dto = new SubmissionDto.SubmissionRequestDto("note", List.of("link1"));
        assertThrows(
            BadRequestException.class,
            () -> submissionService.submitAssessment(
                dto,
                List.of(file),
                assessment.getAssessmentId()
            )
        );
    }

    @Test
    void gradeAssessment_success() {
        setAuthenticatedUser(supervisor.getEmail());

        SubmissionDto.GradingDto gradingDto =
            new SubmissionDto.GradingDto(85, "Well done");

        submissionService.gradeAssessment(
            submission.getSubmissionId(),
            gradingDto
        );

        Submission graded =
            submissionRepository.findById(submission.getSubmissionId())
                .orElseThrow();

        assertEquals(85, graded.getScore());
        assertEquals("Well done", graded.getFeedback());
        assertEquals(GradingStatus.GRADED, graded.getGradingStatus());
    }

    @Test
    void gradeAssessment_submissionNotFound_throwsException() {
        setAuthenticatedUser(supervisor.getEmail());

        SubmissionDto.GradingDto gradingDto =
            new SubmissionDto.GradingDto(70, "Feedback");

        assertThrows(
            NotFoundException.class,
            () -> submissionService.gradeAssessment(
                UUID.randomUUID(),
                gradingDto
            )
        );
    }

    @Test
    void gradeAssessment_alreadyGraded_throwsException() {
        // Pre-grade submission
        submission.setGradingStatus(GradingStatus.GRADED);
        submissionRepository.save(submission);

        setAuthenticatedUser(supervisor.getEmail());

        SubmissionDto.GradingDto gradingDto =
            new SubmissionDto.GradingDto(90, "Updated feedback");

        assertThrows(
            BadRequestException.class,
            () -> submissionService.gradeAssessment(
                submission.getSubmissionId(),
                gradingDto
            )
        );
    }

    @Test
    void gradeAssessment_supervisorTrackMismatch_throwsException() {
        // Authenticate supervisor from a different track
        setAuthenticatedUser(supervisor.getEmail());
        supervisor.setTrackId(UUID.randomUUID());

        SubmissionDto.GradingDto gradingDto =
            new SubmissionDto.GradingDto(60, "Feedback");

        assertThrows(
            AuthorizationException.class,
            () -> submissionService.gradeAssessment(
                submission.getSubmissionId(),
                gradingDto
            )
        );
    }
}

