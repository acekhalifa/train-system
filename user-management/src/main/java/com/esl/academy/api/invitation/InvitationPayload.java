package com.esl.academy.api.invitation;

import com.esl.academy.api.user.UserType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InvitationPayload(
    UUID userId,
    String email,
    UserType userType,
    UUID trackId,
    OffsetDateTime createdAt
) {}
