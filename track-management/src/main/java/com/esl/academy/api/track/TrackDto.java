package com.esl.academy.api.track;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackDto(
    UUID trackId,
    String name,
    String description,
    int duration,
    String learningFocus,
    int numOfSupervisors,
    int numOfInterns,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

    public record AddTrackDto(
        @Size(min = 1, max = 256) @NotNull(message = "name is required") String name,
        String description,
        @Min(value = 1, message = "Duration must be at least 1 month")
        int duration,
        String learningFocus
    ) {}

    public record UpdateTrackDto(
        @Size(min = 1, max = 256) String name,
        String description,
        @Min(value = 1, message = "Duration must be at least 1 month") Integer duration,
        String learningFocus
    ) {}

}
