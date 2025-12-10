package com.esl.academy.api.invitation;

import com.esl.academy.api.intern.InternDto;
import com.esl.academy.api.user.UserDto;
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

@Tag(name = "Invitation")
@RestController
@RequestMapping("api/v1/invitations")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class InvitationController {

    private final InvitationService invitationService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("invite-user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto inviteUser(@RequestBody @Valid InviteUserDto dto) {
        return invitationService.inviteNonInternUser(dto);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternDto inviteIntern(@RequestBody @Valid InviteInternDto dto) {
        return invitationService.inviteIntern(dto);
    }

    @GetMapping("verify")
    public InvitationPayload verify(@RequestParam String token) {
        return invitationService.verifyInvitationToken(token);
    }

    @PostMapping("accept")
    public UserDto acceptInvitation(@RequestParam String token, @RequestBody @Valid AcceptInvitationDto dto) {
        return invitationService.acceptInvitation(token, dto);
    }
}
