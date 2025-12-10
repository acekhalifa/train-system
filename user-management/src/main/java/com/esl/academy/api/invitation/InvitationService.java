package com.esl.academy.api.invitation;

import com.esl.academy.api.appconfig.AppConfig;
import com.esl.academy.api.appconfig.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.constants.CacheId;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.intern.Intern;
import com.esl.academy.api.intern.InternDto;
import com.esl.academy.api.intern.InternMapper;
import com.esl.academy.api.intern.InternRepository;
import com.esl.academy.api.intern.InternStatus;
import com.esl.academy.api.user.*;
import com.esl.academy.api.user.mov.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationService {

    private final UserRepository userRepository;
    private final InternRepository internRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;
    private final AppConfigService appConfigService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.server.publicAppUrl}")
    private String publicAppUrl;

    private String createInvitationToken(User user, UUID trackId) {
        String token = UUID.randomUUID().toString();

        InvitationPayload payload = new InvitationPayload(
            user.getUserId(),
            user.getEmail(),
            user.getUserType(),
            trackId,
            OffsetDateTime.now()
        );

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new BadRequestException("Failed to serialize invitation payload", e);
        }

        String redisKey = CacheId.AUTH_TOKEN.getCacheName() + ":" + token;

        stringRedisTemplate.opsForValue().set(
            redisKey,
            payloadJson,
            tokenExpiryHours(),
            TimeUnit.HOURS
        );

        return token;
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

    private InvitationPayload parseInvitationJson(String json) {
        try {
            return objectMapper.readValue(json, InvitationPayload.class);
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse invitation token", e);
        }
    }

    public UserDto inviteNonInternUser(InviteUserDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("User with email: " + dto.email() + " already exists");
        }
        if (dto.userType() == UserType.INTERN) {
            throw new BadRequestException("Use the intern invitation endpoint.");
        }

        User user = User.builder()
            .email(dto.email())
            .firstName(dto.firstName())
            .lastName(dto.lastName())
            .userType(dto.userType())
            .status(UserStatus.PENDING)
            .build();

        userRepository.save(user);

        String token = createInvitationToken(user, null);

        sendInvitationEmail(user, token);

        return UserMapper.INSTANCE.toDto(user);
    }

    public InternDto inviteIntern(InviteInternDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("Intern with email: " + dto.email() + " already exists");
        }

        if (dto.trackId() == null) {
            throw new BadRequestException("Track ID is required");
        }

        User user = User.builder()
            .email(dto.email())
            .firstName(dto.firstName())
            .lastName(dto.lastName())
            .userType(UserType.INTERN)
            .status(UserStatus.PENDING)
            .build();

        userRepository.save(user);

        Intern intern = new Intern();
        intern.setUser(user);
        intern.setTrackId(dto.trackId());
        intern.setInternStatus(InternStatus.PENDING);

        internRepository.save(intern);

        String token = createInvitationToken(user, dto.trackId());

        sendInvitationEmail(user, token);

        return InternMapper.INSTANCE.toDto(intern);
    }

    public InvitationPayload verifyInvitationToken(String token) {

        String redisKey = CacheId.AUTH_TOKEN.getCacheName() + ":" + token;
        String json = stringRedisTemplate.opsForValue().get(redisKey);

        if (json == null) {
            throw new BadRequestException("Invalid or expired token");
        }

        return parseInvitationJson(json);
    }

    public UserDto acceptInvitation(String token, AcceptInvitationDto dto) {
        InvitationPayload payload = verifyInvitationToken(token);

        User user = userRepository.findById(payload.userId())
            .orElseThrow(() -> new BadRequestException("User not found"));

        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.profilePictureLink() != null) user.setProfilePictureLink(dto.profilePictureLink());

        String hashedPassword = passwordEncoder.encode(dto.password());
        user.setPasswordHash(hashedPassword);
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(OffsetDateTime.now());

        userRepository.save(user);
        internRepository.findById(user.getUserId()).ifPresent(intern -> {
            intern.setInternStatus(InternStatus.ACTIVE);
        });

        // delete token after use
        stringRedisTemplate.delete(CacheId.AUTH_TOKEN.getCacheName() + ":" + token);

        return UserMapper.INSTANCE.toDto(user);
    }

    private int tokenExpiryHours() {
        AppConfig config = appConfigService.getAppConfigById(AppConfigId.INVITATION_EXPIRY_DAYS);
        return Integer.parseInt(config.getValue()) * 24;
    }

}
