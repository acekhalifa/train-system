package com.esl.academy.api.user;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String fullName,
    String firstName,
    String lastName,
    String email,
    UserType userType,
    UserStatus status,
    String profilePictureUrl,
    OffsetDateTime lastLogin,
    Integer loginAttempts,
    OffsetDateTime dateCreated,
    OffsetDateTime dateModified
) {
    public record CreateUserInvitationRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Email address is required")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Track is required")
        UUID trackId,
        @NotNull
        UserType userType,
        @NotBlank(message = "Message is required")
        String message
    ) {}
    public record CreateSuperAdminInvitationRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Email address is required")
        @Email(message = "Email must be valid")
        String email
    ) {}
    public record UpdateProfileRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String name,

        String profilePictureUrl
    ) {}

    public record UpdatePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword
    ) {}

    public record UserInfoSetupDto(
        String name,
        String email,
        UserType userType,
        UUID trackId,
        String trackName
    ) {}
    public record SetupInfoDto(
        String name,
        String email,
        UserType userType,
        UUID trackId,
        String trackName
    ) {}
    public record CompleteSetupRequest(
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
    ) {
        public void validate() {
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match");
            }
        }
    }
}
