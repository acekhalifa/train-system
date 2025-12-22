package com.esl.academy.api.user.invitation;

import com.esl.academy.api.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InvitationDto() {

    public record InviteUserDto(@NotNull @Email String email,
                                @NotBlank @Size(min = 1, max = 50) String firstName,
                                @NotBlank @Size(min = 1, max = 50) String lastName,
                                @NotNull UserType userType,
                                UUID trackId) {}

    public record AcceptInvitationDto(
        @Size(min = 1, max = 50) String firstName,
        @Size(min = 1, max = 50) String lastName,
        String address,
        Long phoneNumber,
        @NotBlank String password
    ) {}

}
