package com.esl.academy.api.intern;

import com.esl.academy.api.user.UserType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InternDto(
    UUID userId,
    UUID trackId,
    UUID certificateId,
    String firstName,
    String lastName,
    String email,
    UserType userType,
    InternStatus internStatus,
    String profilePictureLink,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    String createdBy,
    String updatedBy
) {

    public record UpdateInternStatusDto(@NotNull InternStatus internStatus) {}

    public record UpdateInternTrackDto(@NotNull UUID trackId) {}
}
