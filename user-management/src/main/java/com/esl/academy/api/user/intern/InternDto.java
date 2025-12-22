package com.esl.academy.api.user.intern;

import com.esl.academy.api.track.TrackDto;
import com.esl.academy.api.user.InternStatus;
import com.esl.academy.api.user.user.UserDto;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InternDto(UUID userId,
                        UserDto user,
                        TrackDto track,
                        InternStatus internStatus,
                        OffsetDateTime createdAt,
                        OffsetDateTime updatedAt,
                        String createdBy,
                        String updatedBy) {

    public record UpdateInternStatusDto(@NotNull InternStatus internStatus) {}

    public record UpdateInternTrackDto(@NotNull UUID trackId) {}

}
