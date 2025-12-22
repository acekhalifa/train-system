package com.esl.academy.api.submission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Submission")
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Submit an Assessment")
    @PreAuthorize("hasRole('INTERN')")
    @PostMapping(value = "/{assessmentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionDto.SubmissionResponseDto> submitAssessment(
        @Valid @PathVariable UUID assessmentId,
        @Valid @RequestPart("file") List<MultipartFile> files,
        @Valid @RequestPart("submission") SubmissionDto.SubmissionRequestDto submissionDto
    ) {
        try {
            SubmissionDto.SubmissionResponseDto savedSubmission = submissionService.submitAssessment(
                submissionDto,
                files,
                assessmentId
            );
            return ResponseEntity.ok(savedSubmission);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @Operation(summary = "Grade Submitted Assessment")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("grade/{submissionId}")
    public void gradeSubmission(@Valid @PathVariable UUID submissionId,
                                @Valid @RequestBody SubmissionDto.GradingDto gradingDto){
        submissionService.gradeAssessment(submissionId,gradingDto);
    }

    @Operation(summary = "Get Submitted Assessment for Intern")
    @PreAuthorize("hasRole('INTERN')")
    @GetMapping
    public List<SubmissionDto.SubmittedAssessmentDto> getSubmissions() {
        return submissionService.getAllSubmissionsByIntern();
    }

    @Operation(summary = "Get feedback on Submitted Assessment")
    @PreAuthorize("hasRole('INTERN')")
    @GetMapping("feedback/{assessmentId}")
    public SubmissionDto.GradingDto getAssessmentFeedback(@Valid @PathVariable UUID assessmentId) {
        return submissionService.getFeedbackForSubmission(assessmentId);
    }

    @Operation(summary = "GHet all Submitted assessments in a Track")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @GetMapping("/track/submissions")
    public List<SubmissionDto.InternSubmissionResponseDto> getAllSubmissionsForTrack() {
        return submissionService.getAllSubmissionsForTrack();
    }

    @Operation(summary = "Preview Intern's submission")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @GetMapping("/{submissionId}/preview")
    public SubmissionDto.SubmissionPreviewResponseDto getSubmissionPreview(
        @Valid @PathVariable UUID submissionId) {
        return submissionService.getSubmissionPreview(submissionId);
    }
}
