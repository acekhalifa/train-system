package com.esl.academy.api.user.invitation;

import com.esl.academy.api.app_config.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.services.EmailService;
import com.esl.academy.api.core.services.JwtService;
import com.esl.academy.api.track.Track;
import com.esl.academy.api.user.Intern;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.intern.InternRepository;
import com.esl.academy.api.user.InternStatus;
import com.esl.academy.api.user.UserType;
import com.esl.academy.api.event.InternJoinedTrackEvent;
import com.esl.academy.api.user.user.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static com.esl.academy.api.user.invitation.InvitationDto.*;
import static com.esl.academy.api.user.user.UserMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserService userService;
    private final InternRepository internRepository;
    private final EmailService emailService;
    private final AppConfigService appConfigService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher appEventPublisher;

    @Value("${application.server.publicAppUrl}")
    private String publicAppUrl;

    @Transactional
    public UserDto inviteUser(InviteUserDto dto) {
        if ((dto.userType() == UserType.INTERN || dto.userType() == UserType.SUPERVISOR) && dto.trackId() == null) {
            throw new BadRequestException("Track ID is required for intern/supervisor invitations");
        }

        if (userService.getUserByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("User with email: " + dto.email() + " already exists");
        }

        final var user = User.builder()
            .email(dto.email())
            .firstName(dto.firstName())
            .lastName(dto.lastName())
            .userType(dto.userType())
            .status(UserStatus.PENDING)
            .build();

        final var createdUser = userService.upsertUser(user);

        if (dto.userType() == UserType.INTERN) {
            final var intern = new Intern(user.getUserId());
            intern.setTrack(new Track(dto.trackId()));
            intern.setInternStatus(InternStatus.PENDING);
            internRepository.save(intern);
        }

        final var token = createInvitationToken(user, dto.trackId());

        sendInvitationEmail(user, token);

        return createdUser;
    }

    public UserDto verifyInvitationToken(String token) {
        final var userId = jwtService.getSubject(token);
        return userService.getUser(UUID.fromString(userId))
            .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Transactional
    public UserDto acceptInvitation(String token, AcceptInvitationDto dto) {
        final var user = INSTANCE.map(verifyInvitationToken(token));

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }

        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }

        if (dto.address() != null) {
            user.setAddress(dto.address());
        }

        if (dto.phoneNumber() != null) {
            user.setPhoneNumber(dto.phoneNumber());
        }

        final var hashedPassword = passwordEncoder.encode(dto.password());
        user.setPassword(hashedPassword);
        user.setStatus(UserStatus.ACTIVE);

        final var userDto = userService.upsertUser(user);
        internRepository.findById(user.getUserId()).ifPresent(intern -> {
            intern.setInternStatus(InternStatus.ACTIVE);
            appEventPublisher.publishEvent(new InternJoinedTrackEvent(intern.getTrack()
                    .getTrackId()));
        });

        return userDto;
    }

    private String createInvitationToken(User user, UUID trackId) {
        final var claims = Map.of("trackId", trackId != null ? trackId.toString() : "");
        return jwtService.generateToken(user.getUserId().toString(), claims, tokenExpiryHours(), ChronoUnit.HOURS);
    }

    private void sendInvitationEmail(User user, String token) {
        String invitationLink = publicAppUrl + "/invite?token=" + token;

        String subject = "TCMP Invitation";

        String body = """
        Hi %s,

        You have been invited to TCMP! Click the link below to activate your account:

        %s

        This link expires in %d hours.
        """.formatted(user.getFirstName(), invitationLink, tokenExpiryHours());

        emailService.sendMail(user.getEmail(), subject, body);
    }

    private int tokenExpiryHours() {
        final var config = appConfigService.getAppConfigById(AppConfigId.INVITATION_EXPIRY_DAYS)
            .orElseThrow();
        return Integer.parseInt(config.value()) * 24;
    }


}
