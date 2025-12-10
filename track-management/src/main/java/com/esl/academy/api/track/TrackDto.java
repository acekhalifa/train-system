package com.esl.academy.api.track;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackDto(
    UUID trackId,
    String name,
    String description,
    OffsetDateTime startDate,
    OffsetDateTime endDate,
    int duration,
    Boolean isDeleted,
    String learningFocus,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public record AddTrackDto(
        @Size(min = 1, max = 256)
        @NotNull(message = "name is required")
        String name,

        String description,

        @NotNull(message = "Start date is required")
        OffsetDateTime startDate,

        @NotNull(message = "End date is required")
        OffsetDateTime endDate,

        @Min(value = 1, message = "Duration must be at least 1 month")
        @Max(value = 6, message = "Duration cannot exceed 6 months")
        int duration,

        @NotNull(message = "Learning focus is required")
        String learningFocus
    ) {}

    public record UpdateTrackDto(
        @Size(min = 1, max = 256) String name,
        String description,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        @Min(value = 1) @Max(value = 6) Integer duration,
        String learningFocus
    ) {}
}
