package com.esl.academy.api.invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AcceptInvitationDto(

    @NotNull
    String password,

    // Optional fields (user may update these when accepting invitation)
    @Size(min = 1, max = 50)
    String firstName,

    @Size(min = 1, max = 50)
    String lastName,

    String profilePictureLink

) {

    public AcceptInvitationDto(String password) {
        this(password, null, null, null);

    }
}
