package com.esl.academy.api.assessment;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AssessmentDto(UUID assessmentId,
                            UUID learningResourceId,
                            UUID trackId,
                            String description,
                            OffsetDateTime deadline,
                            boolean isDeleted,
                            PublishedStatus publishedStatus,
                            OffsetDateTime createdAt,
                            OffsetDateTime updatedAt,
                            String createdBy,
                            String modifiedBy) {

    public record AddAssessmentDto(@NotNull UUID learningResourceId,
                                   String description,
                                   @NotNull OffsetDateTime deadline,
                                   PublishedStatus publishedStatus) {}

    public record UpdateAssessmentDto(String description,
                                      OffsetDateTime deadline,
                                      PublishedStatus publishedStatus) {}

}
