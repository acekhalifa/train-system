package com.esl.academy.api.user;

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
import static com.esl.academy.api.user.UserDto.*;

@Tag(name = "User Management")
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create intern invitation")
    @PostMapping("interns/invitations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createInternInvitation(@Valid @RequestBody CreateUserInvitationRequest request) {
         userService.addUserInvitation(request);
    }

    @Operation(summary = "Get setup information by JWT token")
    @GetMapping("/setup/{token}")
    public SetupInfoDto getSetupInfo(@PathVariable String token) {
        return userService.getSetupInfo(token);
    }

    @Operation(summary = "Complete user setup with JWT token validation")
    @PostMapping("/setup/{token}/complete")
    public UserDto completeSetup(
        @PathVariable String token,
        @Valid @RequestBody CompleteSetupRequest request
    ) {
        return userService.completeSetup(token, request);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDto> getAllUsers(
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
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userService.getById(userId);
    }



}
