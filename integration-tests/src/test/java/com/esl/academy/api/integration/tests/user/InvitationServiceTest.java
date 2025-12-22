package com.esl.academy.api.integration.tests.user;

import com.esl.academy.api.app_config.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.constants.CacheId;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.user.intern.InternDto;
import com.esl.academy.api.user.intern.InternRepository;
import com.esl.academy.api.core.services.EmailService;
import com.esl.academy.api.user.invitation.InvitationService;
import com.esl.academy.api.user.invitation.InvitationDto;
import com.esl.academy.api.user.user.UserDto;
import com.esl.academy.api.user.user.UserRepository;
import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InvitationServiceTest extends BaseIntegrationTest {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testEmail;


    @BeforeEach
    void setUp() {
        testEmail = "testuser@example.com";
        Assertions.assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        appConfigService.getAppConfigById(AppConfigId.INVITATION_EXPIRY_DAYS);
    }

    /*
    @Test
    void inviteUser_createsUserAndSendsEmail() {
        InviteUserDto dto = new InviteUserDto("John", "Doe", testEmail, UserType.SUPER_ADMIN);

        UserDto result = invitationService.inviteUser(dto);

        User user = userRepository.findByEmail(testEmail).orElseThrow();
        assertThat(user.getEmail()).isEqualTo(testEmail);
        assertThat(user.getUserType()).isEqualTo(UserType.SUPER_ADMIN);
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);

        verify(emailService, times(1)).sendMail(eq(testEmail), anyString(), anyString());
    }

    @Test
    void inviteIntern_createsUserAndInternAndTokenAndSendsEmail() {
        UUID trackId = UUID.fromString("c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12");
        InvitationDto dto = new InvitationDto("Bob", "Jones", "bob@example.com", trackId);

        InternDto internDto = invitationService.inviteIntern(dto);

        // User saved
        User user = userRepository.findByEmail("bob@example.com").orElseThrow();
        assertThat(user.getUserType()).isEqualTo(UserType.INTERN);

        // Intern saved
        Intern intern = internRepository.findById(user.getUserId()).orElseThrow();
        assertThat(intern.getTrackId()).isEqualTo(trackId);

        // Token exists in Redis
        String redisKeyPrefix = CacheId.AUTH_TOKEN.getCacheName() + ":";
        String token = redisTemplate.keys(redisKeyPrefix + "*").iterator().next();
        assertThat(token).isNotNull();

        // Email sent
        verify(emailService, times(1)).sendMail(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    void inviteIntern_missingTrackId_throwsBadRequest() {
        InvitationDto dto = new InvitationDto("Carol", "White", "carol@example.com", null);

        assertThrows(BadRequestException.class, () -> invitationService.inviteIntern(dto));
    }

    @Test
    void acceptInvitation_updatesUserAndDeletesToken() {
        InviteUserDto dto = new InviteUserDto("Dan", "Brown", "dan@example.com", UserType.SUPER_ADMIN);
        UserDto userDto = invitationService.inviteUser(dto);

        String redisKeyPrefix = CacheId.AUTH_TOKEN.getCacheName() + ":";
        String fullKey = redisTemplate.keys(redisKeyPrefix + "*").iterator().next();
        String json = redisTemplate.opsForValue().get(fullKey);
        System.out.println("REDIS JSON = " + json);
        String token = fullKey.replace(redisKeyPrefix, "");

        AcceptInvitationDto acceptDto = new AcceptInvitationDto("securePassword123");
        UserDto updatedUser = invitationService.acceptInvitation(token, acceptDto);

        User user = userRepository.findByEmail("dan@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("securePassword123", user.getPassword())).isTrue();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

        // Redis token deleted
        assertThat(redisTemplate.opsForValue().get(redisKeyPrefix + token)).isNull();
    }

    @Test
    void verifyInvitationToken_invalidToken_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> invitationService.verifyInvitationToken("nonexistent"));
    }

    @Test
    void inviteUser_duplicateEmail_throwsBadRequest() {
        InviteUserDto dto = new InviteUserDto("Eve", "Green", "eve@example.com", UserType.SUPER_ADMIN);
        invitationService.inviteUser(dto);

        assertThrows(BadRequestException.class, () -> invitationService.inviteUser(dto));
    }

     */
}
