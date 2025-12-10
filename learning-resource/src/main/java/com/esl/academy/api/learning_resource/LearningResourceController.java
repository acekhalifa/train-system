package com.esl.academy.api.learning_resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.esl.academy.api.learning_resource.LearningResourceDto.AddLearningResourceDto;
import com.esl.academy.api.learning_resource.LearningResourceDto.UpdateLearningResourceDto;

import java.util.UUID;

@Tag(name = "Learning resource")
@RestController
@RequestMapping("api/v1/learning-resources")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class LearningResourceController {

    private final LearningResourceService learningResourceService;

    @Operation(summary = "Create a learning resource")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LearningResourceDto addLearningResource(@RequestBody @Valid AddLearningResourceDto dto) {
        return learningResourceService.addLearningResource(dto);
    }

    @Operation(summary = "Update a learning resource")
    @PatchMapping("{learningResourceId}")
    public LearningResourceDto updateLearningResource(@PathVariable UUID learningResourceId, @RequestBody @Valid UpdateLearningResourceDto dto) {
        return learningResourceService.updateLearningResource(learningResourceId, dto);
    }

    @Operation(summary = "Fetch a learning resource by it's ID")
    @GetMapping("{learningResourceId}")
    public LearningResourceDto getLearningResource(@PathVariable UUID learningResourceId) {
        return learningResourceService.getLearningResource(learningResourceId);
    }

    @Operation(summary = "Fetch all learning resources in a track")
    @GetMapping("track/{trackId}")
    public Page<LearningResourceDto> getAllLearningResources(@PathVariable UUID trackId, Pageable pageable) {
        return learningResourceService.getAllLearningResources(trackId, pageable);
    }

    @Operation(summary = "Soft deletes a learning resource")
    @DeleteMapping("{learningResourceId}")
    public void softDeleteLearningResource(@PathVariable UUID learningResourceId) {
        learningResourceService.softDeleteLearningResource(learningResourceId);
    }

    @Operation(summary = "Search learning resources")
    @GetMapping("search")
    public Page<LearningResourceDto> searchLearningResources(
        @RequestParam UUID trackId,
        @RequestParam(required = false) UUID monthId,
        @RequestParam(required = false) UUID weekId,
        @RequestParam(required = false) String resourceTitle,
        @RequestParam(required = false) String description,
        Pageable pageable
    ) {
        return learningResourceService.searchLearningResources(
            trackId,
            monthId,
            weekId,
            resourceTitle,
            description,
            pageable
        );
    }

}
