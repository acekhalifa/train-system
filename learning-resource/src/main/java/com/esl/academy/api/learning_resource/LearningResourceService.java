package com.esl.academy.api.learning_resource;

import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.track.Track;
import com.esl.academy.api.track.TrackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.esl.academy.api.learning_resource.LearningResourceDto.AddLearningResourceDto;
import com.esl.academy.api.learning_resource.LearningResourceDto.UpdateLearningResourceDto;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;
    private final TrackRepository trackRepository;

    public LearningResourceDto addLearningResource(AddLearningResourceDto dto) {
        LearningResource learningResource = LearningResource.builder()
            .trackId(dto.trackId())
            .monthId(dto.monthId())
            .weekId(dto.weekId())
            .resourceTitle(dto.resourceTitle())
            .description(dto.description())
            .isDeleted(false)
            .build();

        return LearningResourceMapper.INSTANCE.toDto(learningResourceRepository.save(learningResource));
    }

    public LearningResourceDto updateLearningResource(UUID learningResourceId, UpdateLearningResourceDto dto) {
        LearningResource learningResource = learningResourceRepository.findById(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource not found"));

        if (dto.resourceTitle() != null) {
            learningResource.setResourceTitle(dto.resourceTitle());
        }
        if (dto.description() != null) {
            learningResource.setDescription(dto.description());
        }

        return LearningResourceMapper.INSTANCE.toDto(
            learningResourceRepository.save(learningResource)
        );
    }

    public LearningResourceDto getLearningResource(UUID learningResourceId) {
        LearningResource learningResource = learningResourceRepository.findById(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource with ID: " + learningResourceId + " not found"));

        return LearningResourceMapper.INSTANCE.toDto(learningResource);
    }

    public Page<LearningResourceDto> getAllLearningResources(UUID trackId, Pageable pageable) {
        trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));

        return learningResourceRepository
            .findByTrackIdAndIsDeletedFalse(trackId, pageable)
            .map(LearningResourceMapper.INSTANCE::toDto);
    }

    public void softDeleteLearningResource(UUID learningResourceId) {
        LearningResource learningResource = learningResourceRepository.findByLearningResourceIdAndIsDeletedFalse(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource not found"));

        learningResource.setDeleted(true);
        learningResourceRepository.save(learningResource);
    }

    public Page<LearningResourceDto> searchLearningResources(
        UUID trackId,
        UUID monthId,
        UUID weekId,
        String resourceTitle,
        String description,
        Pageable pageable
    ) {
        trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));

        Specification<LearningResource> spec = Specification.allOf(
            LearningResourceSpecification.byTrackId(trackId),
            LearningResourceSpecification.byMonthId(monthId),
            LearningResourceSpecification.byWeekId(weekId),
            LearningResourceSpecification.byResourceTitle(resourceTitle),
            LearningResourceSpecification.byDescription(description)
        );

        return learningResourceRepository.findAll(spec, pageable)
            .map(LearningResourceMapper.INSTANCE::toDto);
    }
}

