package com.esl.academy.api.assessment;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.learning_resource.LearningResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.esl.academy.api.assessment.AssessmentDto.AddAssessmentDto;
import com.esl.academy.api.assessment.AssessmentDto.UpdateAssessmentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.esl.academy.api.learning_resource.LearningResourceMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final LearningResourceService learningResourceService;

    public AssessmentDto addAssessment(AddAssessmentDto dto) {
        final var learningResourceDto = learningResourceService
            .getLearningResource(dto.learningResourceId());

        final var assessment = Assessment.builder()
            .learningResource(INSTANCE.map(learningResourceDto))
            .deadline(dto.deadline())
            .isDeleted(false)
            .publishedStatus(dto.publishedStatus())
            .build();

        if (dto.description() != null) {
            assessment.setDescription(dto.description());
        }

        return AssessmentMapper.INSTANCE.map(assessmentRepository.save(assessment));
    }

    public AssessmentDto updateAssessment(UUID assessmentId, UpdateAssessmentDto dto) {
        final var assessment = assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new NotFoundException("Assessment not found"));

        if (dto.description() != null) {
            assessment.setDescription(dto.description());
        }
        if (dto.deadline() != null) {
            assessment.setDeadline(dto.deadline());
        }
        if (dto.publishedStatus() != null) {
            assessment.setPublishedStatus(dto.publishedStatus());
        }

        return AssessmentMapper.INSTANCE.map(assessmentRepository.save(assessment));
    }

    public AssessmentDto getAssessmentById(UUID assessmentId) {
        final var assessment = assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new NotFoundException("Assessment not found"));

        return AssessmentMapper.INSTANCE.map(assessment);
    }

    public Page<AssessmentDto> getAllAssessments(UUID trackId, Pageable pageable) {

        // TODO: authService to be imported when auth is merged to main
        // String userId = authService.getCurrentUserId().toString();
        String userId = "11111111-1111-1111-1111-111111111111";

        Page<Assessment> assessments;

        if (trackId == null) {
            assessments = assessmentRepository.findByCreator(userId, pageable);
        } else {
            assessments = assessmentRepository.findByTrackAndCreator(trackId, userId, pageable);
        }

        return assessments.map(AssessmentMapper.INSTANCE::map);
    }

    public int countAssessmentsInTrack(UUID trackId) {
        return assessmentRepository.countAssessmentsInTrack(trackId);
    }

    public Optional<Assessment> getById(UUID id) {
        return assessmentRepository.findById(id);
    }

    public List<Assessment> getAssessmentsByTrack(UUID trackId) {
        return assessmentRepository.findByLearningResource_Track_TrackId(trackId);
    }
}
