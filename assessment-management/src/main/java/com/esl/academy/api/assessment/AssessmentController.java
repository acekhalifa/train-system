package com.esl.academy.api.assessment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.esl.academy.api.assessment.AssessmentDto.AddAssessmentDto;
import com.esl.academy.api.assessment.AssessmentDto.UpdateAssessmentDto;

import java.util.UUID;

@Tag(name = "Assessment")
@RestController
@RequestMapping("api/v1/assessments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Operation(summary = "Add an assessment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentDto addAssessment(@RequestBody @Valid AddAssessmentDto dto) {
        return assessmentService.addAssessment(dto);
    }

    @Operation(summary = "Update an assessment")
    @PatchMapping("{assessmentId}")
    public AssessmentDto updateAssessment(
        @PathVariable UUID assessmentId,
        @RequestBody @Valid UpdateAssessmentDto dto
    ) {
        return assessmentService.updateAssessment(assessmentId, dto);
    }

    @Operation(summary = "Get an assessment by Id")
    @GetMapping("{assessmentId}")
    public AssessmentDto getAssessmentById(@PathVariable UUID assessmentId) {
        return assessmentService.getAssessmentById(assessmentId);
    }

    @Operation(summary = "Get all assessments", description = "Fetches all assessments made by a supervisor. Optional filter by track")
    @GetMapping
    public Page<AssessmentDto> getAllAssessments(@RequestParam(required = false) UUID trackId,
                                                 @PageableDefault Pageable pageable) {
        return assessmentService.getAllAssessments(trackId, pageable);
    }

    @Operation(summary = "Count all assessments in a track")
    @GetMapping("track/{trackId}")
    public int countAllAssessmentsInATrack(UUID trackId) {
        return assessmentService.countAssessmentsInTrack(trackId);
    }
}
