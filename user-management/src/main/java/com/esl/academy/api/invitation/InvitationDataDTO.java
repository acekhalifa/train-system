package com.esl.academy.api.invitation;

import com.esl.academy.api.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InvitationDataDTO {

    public record InvitationTokenData(
        @NotNull UUID userId,
        @Email String email,
        @NotNull String fullName,
       @NotNull UserType userType,
        UUID trackId
    ) {}


    public record InvitationUrlResponse(
        String invitationUrl,
        String token,
        String expiresAt
    ) {}
}
