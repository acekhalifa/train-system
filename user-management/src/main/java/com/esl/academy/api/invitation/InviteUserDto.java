package com.esl.academy.api.invitation;

import com.esl.academy.api.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InviteUserDto(
    @NotNull @Size(min = 1, max = 50) String firstName,
    @NotNull @Size(min = 1, max = 50) String lastName,
    @NotNull @Email String email,
    @NotNull UserType userType
) {
}
