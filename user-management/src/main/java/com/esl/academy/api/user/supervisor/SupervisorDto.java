package com.esl.academy.api.user.supervisor;

import com.esl.academy.api.track.TrackDto;
import com.esl.academy.api.user.SupervisorStatus;
import com.esl.academy.api.user.user.UserDto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record SupervisorDto(
    UUID userId,
    UserDto user,
    SupervisorStatus supervisorStatus,
    Set<TrackDto> tracks,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    String createdBy,
    String updatedBy) {}
