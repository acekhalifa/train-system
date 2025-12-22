package com.esl.academy.api.user.user;

import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.UserType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(UUID userId,
                      String firstName,
                      String lastName,
                      String email,
                      UserType userType,
                      UserStatus status,
                      String address,
                      Long phoneNumber,
                      String profilePictureLink,
                      OffsetDateTime createdAt,
                      OffsetDateTime updatedAt,
                      String createdBy,
                      String updatedBy) {

    public record UpdateUserDto(String firstName,
                                String lastName,
                                String email,
                                String profilePictureLink) {}

    public record UpdateUserTypeDto(@NotNull UserType userType) {}

    public record UpdateUserStatusDto(@NotNull UserStatus status) {}

}
