package com.esl.academy.api.user.invitation;

import com.esl.academy.api.user.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.esl.academy.api.user.invitation.InvitationDto.*;

@Tag(name = "Invitation")
@RestController
@RequestMapping("api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Invite any type of user into the system", security = @SecurityRequirement(name = "Authorization"))
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto inviteUser(@RequestBody @Valid InviteUserDto dto) {
        return invitationService.inviteUser(dto);
    }

    @Operation(summary = "Verify invitation token and get user details")
    @GetMapping("verify")
    public UserDto verify(@RequestParam String token) {
        return invitationService.verifyInvitationToken(token);
    }

    @Operation(summary = "Accept invitation and set up user account")
    @PostMapping("accept")
    public UserDto acceptInvitation(@RequestParam String token, @RequestBody @Valid AcceptInvitationDto dto) {
        return invitationService.acceptInvitation(token, dto);
    }

}
