package com.esl.academy.api.learning_resource;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LearningResourceDto(UUID learningResourceId,
                                  String track,
                                  String month,
                                  String week,
                                  String resourceTitle,
                                  String description,
                                  Boolean isDeleted,
                                  OffsetDateTime createdAt,
                                  OffsetDateTime updatedAt,
                                  String createdBy,
                                  String modifiedBy) {

    public record AddLearningResourceDto(@NotNull UUID trackId,
                                         @NotNull UUID monthId,
                                         @NotNull UUID weekId,
                                         @NotNull @Min(1) @Max(100) String resourceTitle,
                                         @NotNull String description) {}

    public record UpdateLearningResourceDto(@Min(1) @Max(100) String resourceTitle,
                                            String description) {}

}
