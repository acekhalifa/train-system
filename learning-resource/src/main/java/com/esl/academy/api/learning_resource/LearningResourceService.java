package com.esl.academy.api.learning_resource;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.options.option.Option;
import com.esl.academy.api.options.option.OptionService;
import com.esl.academy.api.options.option_type.OptionType;
import com.esl.academy.api.options.option_type.OptionTypeService;
import com.esl.academy.api.track.Track;
import com.esl.academy.api.track.TrackService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.esl.academy.api.learning_resource.LearningResourceDto.UpdateLearningResourceDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;
    private final TrackService trackService;
    private final OptionTypeService optionTypeService;
    private final OptionService optionService;

    @Transactional
    public void generateLearningResourceTemplates(Track track) {
        int totalMonths = track.getDuration();
        int weeksPerMonth = 4;

        OptionType monthType = optionTypeService.findByName("month")
            .orElseThrow(() -> new NotFoundException("Month option type not found"));

        OptionType weekType = optionTypeService.findByName("week")
            .orElseThrow(() -> new NotFoundException("Week option type not found"));

        List<Option> monthOptions = optionService.findByOptionType(monthType);
        List<Option> weekOptions = optionService.findByOptionType(weekType);

        for (int monthIndex = 0; monthIndex < totalMonths; monthIndex++) {

            Option month = monthOptions.get(monthIndex);

            for (int weekIndex = 0; weekIndex < weeksPerMonth; weekIndex++) {

                Option week = weekOptions.get(weekIndex);

                LearningResource lr = LearningResource.builder()
                    .track(track)
                    .month(month)
                    .week(week)
                    .resourceTitle("Placeholder title")
                    .description("Placeholder description")
                    .isDeleted(false)
                    .build();

                learningResourceRepository.save(lr);
            }
        }
    }


    @Transactional
    public LearningResourceDto updateLearningResource(UUID learningResourceId, UpdateLearningResourceDto dto) {
        final var learningResource = learningResourceRepository.findById(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource not found"));

        if (dto.resourceTitle() != null) {
            learningResource.setResourceTitle(dto.resourceTitle());
        }
        if (dto.description() != null) {
            learningResource.setDescription(dto.description());
        }

        return LearningResourceMapper.INSTANCE.map(
            learningResourceRepository.save(learningResource)
        );
    }

    public LearningResourceDto getLearningResource(UUID learningResourceId) {
        final var learningResource = learningResourceRepository.findById(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource with ID: " + learningResourceId + " not found"));

        return LearningResourceMapper.INSTANCE.map(learningResource);
    }

    public Page<LearningResourceDto> getAllLearningResources(UUID trackId, Pageable pageable) {
        trackService.getTrackById(trackId);

        return learningResourceRepository
            .findByTrackTrackIdAndIsDeletedFalse(trackId, pageable)
            .map(LearningResourceMapper.INSTANCE::map);
    }

    @Transactional
    public void softDeleteLearningResource(UUID learningResourceId) {
        final var learningResource = learningResourceRepository.findByLearningResourceIdAndIsDeletedFalse(learningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource not found"));

        learningResource.setDeleted(true);
        learningResourceRepository.save(learningResource);
    }

    public Page<LearningResourceDto> searchLearningResources(UUID trackId, UUID monthId, UUID weekId,
                                                             String resourceTitle, String description,
                                                             Pageable pageable) {
        trackService.getTrackById(trackId);

        Specification<LearningResource> spec = Specification.allOf(
            LearningResourceSpecification.byTrackId(trackId),
            LearningResourceSpecification.byMonthId(monthId),
            LearningResourceSpecification.byWeekId(weekId),
            LearningResourceSpecification.byResourceTitle(resourceTitle),
            LearningResourceSpecification.byDescription(description)
        );

        return learningResourceRepository.findAll(spec, pageable)
            .map(LearningResourceMapper.INSTANCE::map);
    }
}

