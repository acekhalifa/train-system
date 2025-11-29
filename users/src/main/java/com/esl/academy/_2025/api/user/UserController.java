package com.esl.academy._2025.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.esl.academy._2025.api.user.UserDto.*;

@Tag(name = "User Management")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register new user (Supervisor or Intern)")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return userService.registerUser(request);
    }

    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers(
        @RequestParam(required = false) UserType userType,
        @RequestParam(required = false) UserStatus status
    ) {
        if (userType != null) {
            return userService.getUsersByType(userType);
        }
        if (status != null) {
            return userService.getUsersByStatus(status);
        }
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR')")
    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Get user by email")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR')")
    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Update user profile")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR')")
    @PutMapping("/{userId}")
    public UserResponse updateUser(
        @PathVariable UUID userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(userId, request);
    }

    @Operation(summary = "Activate user")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{userId}/activate")
    public UserResponse activateUser(@PathVariable UUID userId) {
        return userService.activateUser(userId);
    }

    @Operation(summary = "Deactivate user")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{userId}/deactivate")
    public UserResponse deactivateUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }

    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

    @Operation(summary = "Reset login attempts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{userId}/reset-login-attempts")
    public UserResponse resetLoginAttempts(@PathVariable UUID userId) {
        return userService.resetLoginAttempts(userId);
    }

    @Operation(summary = "Change password")
    @PatchMapping("/{userId}/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
        @PathVariable UUID userId,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userId, request);
    }
}
