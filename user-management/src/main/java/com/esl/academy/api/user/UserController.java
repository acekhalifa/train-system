package com.esl.academy.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.esl.academy.api.user.UserDto.UpdateUserTypeDto;
import static com.esl.academy.api.user.UserDto.UpdateUserStatusDto;

@Tag(name = "User")
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update any user, including super_admin, supervisor, and intern")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    @PatchMapping("{userId}")
    public UserDto updateUser(
        @PathVariable UUID userId,
        @Valid @RequestBody UserDto.UpdateUserDto dto
    ) {
        return userService.updateUser(userId, dto);
    }

    @Operation(summary = "Change the role of any user in the system")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("{userId}/role")
    public UserDto changeUserRole(
        @PathVariable UUID userId,
        @RequestBody @Valid UpdateUserTypeDto dto
    ) {
        return userService.changeUserRole(userId, dto);
    }

    @Operation(summary = "Change the status of any user in the system")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("{userId}/status")
    public UserDto changeUserStatus(
        @PathVariable UUID userId,
        @RequestBody @Valid UpdateUserStatusDto dto
    ) {
        return userService.changeUserStatus(userId, dto);
    }

    @Operation(summary = "Fetch all supervisors.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("supervisors")
    public Page<UserDto> getAllSupervisors(Pageable pageable) {
        return userService.getAllSupervisors(pageable);
    }

    @Operation(summary = "Fetch all super_admins.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("superadmins")
    public Page<UserDto> getAllSuperAdmins(Pageable pageable) {
        return userService.getAllSuperAdmins(pageable);
    }

    @Operation(summary = "Fetch all users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Fetch any user by the userId")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

    @Operation()
    @GetMapping("supervisors/search")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<UserDto> searchSupervisors(
        @RequestParam(required = false) UUID assignedTrackId,
        @RequestParam(required = false) UserStatus status,
        @RequestParam(required = false) OffsetDateTime createdAt,
        @RequestParam(required = false) String name,
        Pageable pageable
    ) {
        return userService.searchSupervisors(
            assignedTrackId,
            status,
            createdAt,
            name,
            pageable
        );
    }

    @Operation()
    @GetMapping("superadmins/search")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<UserDto> searchSuperAdmins(
        @RequestParam(required = false) UserStatus status,
        @RequestParam(required = false) OffsetDateTime createdAt,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        Pageable pageable
    ) {
        return userService.searchSuperAdmins(
            status,
            createdAt,
            name,
            email,
            pageable
        );

    }
}
