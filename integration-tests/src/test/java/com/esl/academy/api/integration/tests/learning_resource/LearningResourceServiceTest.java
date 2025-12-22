package com.esl.academy.api.integration.tests.learning_resource;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.learning_resource.LearningResource;
import com.esl.academy.api.learning_resource.LearningResourceDto;
import com.esl.academy.api.learning_resource.LearningResourceDto.UpdateLearningResourceDto;
import com.esl.academy.api.learning_resource.LearningResourceDto.AddLearningResourceDto;
import com.esl.academy.api.learning_resource.LearningResourceRepository;
import com.esl.academy.api.learning_resource.LearningResourceService;
import com.esl.academy.api.options.option.OptionRepository;
import com.esl.academy.api.options.option_type.OptionTypeRepository;
import com.esl.academy.api.track.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LearningResourceServiceTest extends BaseIntegrationTest {

    @Autowired
    private LearningResourceService learningResourceService;

    @Autowired
    private LearningResourceRepository learningResourceRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private OptionTypeRepository optionTypeRepository;

    private UUID existingLearningResourceId;

    private UUID existingTrackId;

    private UUID monthId;

    private UUID weekId;

    @BeforeEach
    void setup() {
        existingLearningResourceId = UUID.fromString("b4c46104-dac7-430d-9e22-e8285d192974");
        existingTrackId = UUID.fromString("c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12");
        monthId = UUID.randomUUID();
        weekId = UUID.randomUUID();
    }

    @Test
    void updateLearningResource_withValidData_shouldUpdate() {
        LearningResource learningResource = learningResourceRepository.findById(existingLearningResourceId)
            .orElseThrow(() -> new NotFoundException("Learning resource with ID not found"));

        UpdateLearningResourceDto updateDto = new UpdateLearningResourceDto(
            "Updated REST API Basics",
            "Updated description for Restful APIs."
        );

        LearningResourceDto updated = learningResourceService.updateLearningResource(existingLearningResourceId, updateDto);

        assertNotNull(updated);
        assertEquals(existingLearningResourceId, updated.learningResourceId());
        assertEquals("Updated REST API Basics", updated.resourceTitle());
        assertEquals("Updated description for Restful APIs.", updated.description());
    }

    @Test
    void getLearningResource_withCorrectId_shouldFetch() {
        LearningResourceDto dto = learningResourceService.getLearningResource(existingLearningResourceId);

        assertNotNull(dto);
        assertNotNull(dto.learningResourceId());
        assertEquals("REST API Basics", dto.resourceTitle());
        assertEquals("Introduction to building RESTful APIs with Java and Spring Boot.", dto.description());
    }

    @Test
    void getLearningResource_withIncorrectId_shouldThrowNotFoundException() {
        UUID invalidId = UUID.fromString("1c1711c5-b7ff-4133-b79e-dfd6a49708ce");

        assertThrows(NotFoundException.class, () ->
            learningResourceService.getLearningResource(invalidId));
    }

    @Test
    void getAllLearningResource_withValidTrackId_shouldFetch() {
        PageRequest page = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

        Page<LearningResourceDto> result = learningResourceService.getAllLearningResources(existingTrackId, page);

        assertNotNull(result);
        assertEquals(5, result.getTotalElements());

        assertTrue(result.getContent().stream()
            .anyMatch(r -> r.resourceTitle().equals("REST API Basics")));
        assertTrue(result.getContent().stream()
            .anyMatch(r -> r.resourceTitle().equals("Spring Boot Dependency Injection")));
        assertTrue(result.getContent().stream()
            .anyMatch(r -> r.resourceTitle().equals("Database Design")));
        assertTrue(result.getContent().stream()
            .anyMatch(r -> r.resourceTitle().equals("REST Security")));
        assertTrue(result.getContent().stream()
            .anyMatch(r -> r.resourceTitle().equals("Advanced JPA")));
    }


    @Test
    void getAllLearningResource_withInvalidTrackId_shouldThrowNotFoundException() {
        UUID invalidTrackId = UUID.fromString("b4c46104-dac7-430d-9e23-e8285d192974");

        assertThrows(NotFoundException.class, () ->
            learningResourceService.getAllLearningResources(invalidTrackId, PageRequest.of(0, 10)));
    }

    @Test
    void softDeleteLearningResource_shouldDelete() {
        learningResourceService.softDeleteLearningResource(existingLearningResourceId);

        LearningResource deleted = learningResourceRepository.findById(existingLearningResourceId)
            .orElseThrow();

        assertTrue(deleted.isDeleted());
    }

    @Test
    void searchLearningResources_withOnlyTrackId_shouldReturnCorrectResults() {
        PageRequest page = PageRequest.of(0, 10);

        Page<LearningResourceDto> result = learningResourceService.searchLearningResources(
            existingTrackId,
            null,
            null,
            "rest ap",
            null,
            page
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("REST API Basics", result.getContent().getFirst().resourceTitle());
    }

    @Test
    void searchLearningResources_withFilters_shouldReturnCorrectResults() {
        PageRequest page = PageRequest.of(0, 10);

        UUID monthOptionTypeId = optionTypeRepository.findByName("Month")
            .orElseThrow(() -> new NotFoundException("Month option type not found"))
            .getOptionTypeId();

        UUID februaryMonthId = optionRepository.findByOptionType_OptionTypeId(monthOptionTypeId).stream()
            .filter(option -> "February".equalsIgnoreCase(option.getName()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("February option not found"))
            .getOptionId();

        UUID weekOptionTypeId = optionTypeRepository.findByName("Week")
            .orElseThrow(() -> new NotFoundException("Week option type not found"))
            .getOptionTypeId();

        UUID week2Id = optionRepository.findByOptionType_OptionTypeId(weekOptionTypeId).stream()
            .filter(option -> "2".equals(option.getName()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Week 2 option not found"))
            .getOptionId();

        Page<LearningResourceDto> result = learningResourceService.searchLearningResources(
            existingTrackId,
            februaryMonthId,
            week2Id,
            "spring",
            null,
            page
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Spring Boot Dependency Injection", result.getContent().get(0).resourceTitle());
    }


    @Test
    void searchLearningResources_invalidTrackId_shouldThrowNotFoundException() {
        PageRequest page = PageRequest.of(0, 10);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            learningResourceService.searchLearningResources(
                UUID.randomUUID(),  // invalid trackId
                null,
                null,
                null,
                null,
                page
            )
        );

        assertTrue(exception.getMessage().contains("Track not found"));
    }


}
