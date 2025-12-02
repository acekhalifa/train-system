package com.esl.academy.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public enum UserType {
    SUPER_ADMIN,
    SUPERVISOR,
    INTERN
}

//package com.esl.academy.api.user;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import java.time.OffsetDateTime;
//import java.util.UUID;
//
//public record UserDto(

//) {
//    public record CreateInternInvitationRequest(
//        @NotBlank(message = "Name is required")
//        @Size(max = 100, message = "Name must not exceed 100 characters")
//        String name,
//
//        @NotBlank(message = "Email address is required")
//        @Email(message = "Email must be valid")
//        @Size(max = 100, message = "Email must not exceed 100 characters")
//        String email,
//
//        @NotNull(message = "Track is required")
//        UUID trackId,
//
//        @NotBlank(message = "Message is required")
//        String message
//    ) {}
//
//    public record CreateSuperAdminInvitationRequest(
//        @NotBlank(message = "Name is required")
//        @Size(max = 100, message = "Name must not exceed 100 characters")
//        String name,
//
//        @NotBlank(message = "Email address is required")
//        @Email(message = "Email must be valid")
//        @Size(max = 100, message = "Email must not exceed 100 characters")
//        String email
//    ) {}
//
//    public record UserResponse(
//        UUID id,
//        String firstName,
//        String lastName,
//        String email,
//        UserType userType,
//        UserStatus status,
//        String profilePictureLink,
//        OffsetDateTime lastLogin,
//        Integer loginAttempts,
//        OffsetDateTime dateCreated
//    ) {}
//
//}
//
