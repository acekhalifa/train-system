package com.esl.academy._2025.api.user;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public class UserDto {

    public record RegisterUserRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotNull(message = "User type is required")
        UserType userType,

        UUID trackId
    ) {
        public void validate() {
            if (userType == UserType.INTERN && trackId == null) {
                throw new IllegalArgumentException("Track ID is required for INTERN users");
            }
            if (userType == UserType.SUPER_ADMIN) {
                throw new IllegalArgumentException("Cannot register SUPER_ADMIN users through this endpoint");
            }
        }
    }

    public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String profilePictureLink
    ) {}

    public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8) String newPassword
    ) {}

    // Response DTOs
    public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserType userType,
        UserStatus status,
        String profilePictureLink,
        OffsetDateTime lastLogin,
        Integer loginAttempts,
        OffsetDateTime dateCreated
    ) {}

    public record SupervisorResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        String profilePictureLink,
        OffsetDateTime dateCreated
    ) {}

    public record InternResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        UUID trackId,
        String trackName,
        String profilePictureLink,
        OffsetDateTime dateCreated
    ) {}
}
