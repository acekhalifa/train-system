package com.esl.academy.api.invitation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record InviteInternDto(
    @NotNull @Size(min = 1, max = 50) String firstName,
    @NotNull @Size(min = 1, max = 50) String lastName,
    @NotNull @Email String email,
    @NotNull UUID trackId
) {
}
