package com.esl.academy.api.user;

import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.ConflictException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.intern.Intern;
import com.esl.academy.api.intern.InternRepository;
import com.esl.academy.api.intern.InternStatus;
import com.esl.academy.api.invitation.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.esl.academy.api.invitation.InvitationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.esl.academy.api.user.UserDto.*;
import static com.esl.academy.api.user.UserMapper.INSTANCE;
import static com.esl.academy.api.invitation.InvitationDataDTO.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final InternRepository internRepository;
    private final InvitationTokenService invitationTokenService;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addUserInvitation(CreateUserInvitationRequest request) {
        log.info("Creating user invitation for email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        validateTrackExists(request.trackId());
        if(request.userType() == UserType.INTERN) {
            createInternInvitation(request);
        } else if (request.userType() == UserType.SUPERVISOR) {
            createSupervisorInvitation(request);
        } else {
            throw new UnsupportedOperationException("User type " + request.userType() + " is not supported for invitations");
        }

    }

    public void addSuperAdminUser(CreateSuperAdminInvitationRequest request) {
        log.info("Creating SUPER ADMIN user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        User user = User.builder()
            .userId(UUID.randomUUID())
            .email(request.email())
            .firstName(INSTANCE.extractFirstName(request.name()))
            .lastName(INSTANCE.extractLastName(request.name()))
            .userType(UserType.SUPER_ADMIN)
            .status(UserStatus.PENDING)
            .createdBy("system")
            .build();

        userRepository.save(user);
        log.info("SUPER_ADMIN user created with ID: {}", user.getUserId());

        InvitationTokenData tokenData = new InvitationTokenData(
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getUserType(),
            null
        );
        String invitationToken = invitationTokenService.generateInvitationToken(tokenData);
        String invitationUrl = invitationTokenService.generateInvitationUrl(invitationToken);
        log.info("Invitation token generated for SUPER ADMIN user: {}", user.getUserId());

        emailService.sendSuperAdminInvitation(
            user.getEmail(),
            user.getFullName(),
            invitationUrl
        );
    }

    public SetupInfoDto getSetupInfo(String token) {
        log.info("Retrieving setup info from JWT token");
        InvitationTokenData tokenData = invitationTokenService.validateAndExtractToken(token);
        User user = findUserById(tokenData.userId());
        validateTokenDataMatchesUser(tokenData, user);

        String trackName = null;
        if (user.getUserType() == UserType.INTERN && tokenData.trackId() != null) {
            trackName = "Track Name"; // TODO: Fetch from track repository
        }

        return new SetupInfoDto(
            tokenData.fullName(),
            tokenData.email(),
            tokenData.userType(),
            tokenData.trackId(),
            trackName
        );
    }

    public UserDto completeSetup(String token, CompleteSetupRequest request) {
        log.info("Completing setup with JWT token");
        request.validate();
        InvitationTokenData tokenData = invitationTokenService.validateAndExtractToken(token);
        User user = findUserById(tokenData.userId());
        validateTokenDataMatchesUser(tokenData, user);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);

        if (user.getUserType() == UserType.INTERN) {
            Intern intern = internRepository.findById(user.getUserId())
                .orElseThrow(() -> new NotFoundException("Intern record not found"));
            intern.setInternStatus(InternStatus.ACTIVE);
            internRepository.save(intern);
        }
        user = userRepository.save(user);
        log.info("Setup completed for user: {}", user.getUserId());
        return INSTANCE.toUserDto(user);
    }



    private void createInternInvitation(CreateUserInvitationRequest request) {
        validateTrackExists(request.trackId());
        User user = User.builder()
            .userId(UUID.randomUUID())
            .email(request.email())
            .firstName(INSTANCE.extractFirstName(request.name()))
            .lastName(INSTANCE.extractLastName(request.name()))
            .userType(UserType.INTERN)
            .status(UserStatus.PENDING)
            .createdBy("system")
            .build();
        user = userRepository.save(user);
        log.info("Intern invitation created with ID: {}", user.getUserId());
        Intern intern = new Intern(user.getUserId(), request.trackId(), InternStatus.PENDING);
        internRepository.save(intern);

        InvitationTokenData tokenData = new InvitationTokenData(
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getUserType(),
            request.trackId()
        );
        String invitationToken = invitationTokenService.generateInvitationToken(tokenData);
        String invitationUrl = invitationTokenService.generateInvitationUrl(invitationToken);

        log.info("Invitation token generated for user: {}", user.getUserId());
        emailService.sendInternInvitation(
            user.getEmail(),
            user.getFullName(),
            invitationUrl,
            request.message()
        );

    }

    private void createSupervisorInvitation(CreateUserInvitationRequest request) {
        User user = User.builder()
            .userId(UUID.randomUUID())
            .email(request.email())
            .firstName(INSTANCE.extractFirstName(request.name()))
            .lastName(INSTANCE.extractLastName(request.name()))
            .userType(UserType.SUPERVISOR)
            .status(UserStatus.PENDING)
            .createdBy("system")
            .build();
        user = userRepository.save(user);
        log.info("Supervisor invitation created with ID: {}", user.getUserId());
        InvitationTokenData tokenData = new InvitationTokenData(
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getUserType(),
            request.trackId()
        );
        String invitationToken = invitationTokenService.generateInvitationToken(tokenData);
        String invitationUrl = invitationTokenService.generateInvitationUrl(invitationToken);

        log.info("Invitation token generated for supervisor: {}", user.getUserId());
        emailService.sendInternInvitation(
            user.getEmail(),
            user.getFullName(),
            invitationUrl,
            request.message()
        );
    }

    public UserDto getById(UUID userId) {
        return userRepository.findById(userId)
            .map(INSTANCE::toUserDto)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    public List<UserDto> getAllUsers() {
        return INSTANCE.toUserDtoList(userRepository.findAll());
    }

    public List<UserDto> getUsersByType(UserType userType) {
        return INSTANCE.toUserDtoList(userRepository.findByUserType(userType));

    }

    public List<UserDto> getUsersByStatus(UserStatus status) {
        return INSTANCE.toUserDtoList(userRepository.findByStatus(status));

    }
    public UserDto updateProfile(UUID userId, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userId);
        User user = findUserById(userId);
        user.setFirstName(INSTANCE.extractFirstName(request.name()));
        user.setLastName(INSTANCE.extractLastName(request.name()));
        if (request.profilePictureUrl() != null) {
            user.setProfilePictureLink(request.profilePictureUrl());
        }
        user = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);
        return userMapper.toUserDto(user);
    }

    public void updatePassword(UUID userId, UpdatePasswordRequest request) {
        log.info("Updating password for user: {}", userId);
        User user = findUserById(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password updated for user: {}", userId);
    }

    public void deactivateUser(UUID userId) {
        log.info("Deactivating user: {}", userId);
        User user = findUserById(userId);
        user.setStatus(UserStatus.INACTIVE);
        user = userRepository.save(user);
        log.info("User deactivated: {}", userId);
    }

private void validateTokenDataMatchesUser(InvitationTokenData tokenData, User user) {
    if (!tokenData.email().equalsIgnoreCase(user.getEmail())) {
        throw new BadRequestException("Email mismatch");
    }

    if (!tokenData.fullName().equals(user.getFullName())) {
        throw new BadRequestException("Name mismatch");
    }

    if (tokenData.userType() != user.getUserType()) {
        throw new BadRequestException("User type mismatch");
    }

    if (user.getUserType() == UserType.INTERN && tokenData.trackId() != null) {
        Intern intern = internRepository.findById(user.getUserId())
            .orElseThrow(() -> new NotFoundException("Intern record not found"));

        if (!intern.getTrackId().equals(tokenData.trackId())) {
            throw new BadRequestException("Track mismatch");
        }
    }
}

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private void validateTrackExists(UUID trackId) {

        // trackRepository.findById(trackId)
        //     .orElseThrow(() -> new NotFoundException("Track not found"));
    }
}
