package com.esl.academy._2025.api.user;

import com.esl.academy.api.core.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.esl.academy._2025.api.user.UserDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SupervisorRepository supervisorRepository;
    private final InternRepository internRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Register a new user (Supervisor or Intern)
     */
    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Registering new user with email: {}", request.email());

        // 1. Validate request
        request.validate();

        // 2. Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        // 3. Validate track exists for interns
        if (request.userType() == UserType.INTERN) {
            validateTrackExists(request.trackId());
        }

        // 4. Create user entity
        User user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .userType(request.userType())
            .status(UserStatus.ACTIVE)
            .loginAttempts(0)
            .build();

        user = userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        // 5. Create type-specific record
        createTypeSpecificRecord(user.getId(), request.userType(), request.trackId());

        return userMapper.toResponse(user);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
    }

    /**
     * Get users by type
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType).stream()
            .map(userMapper::toResponse)
            .toList();
    }

    /**
     * Get users by status
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream()
            .map(userMapper::toResponse)
            .toList();
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Updating user: {}", userId);

        User user = findUserById(userId);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setProfilePictureLink(request.profilePictureLink());

        user = userRepository.save(user);
        log.info("User updated: {}", userId);

        return userMapper.toResponse(user);
    }

    /**
     * Activate user
     */
    @Transactional
    public UserResponse activateUser(UUID userId) {
        log.info("Activating user: {}", userId);

        User user = findUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        user.setLoginAttempts(0);

        user = userRepository.save(user);
        log.info("User activated: {}", userId);

        return userMapper.toResponse(user);
    }

    /**
     * Deactivate user
     */
    @Transactional
    public UserResponse deactivateUser(UUID userId) {
        log.info("Deactivating user: {}", userId);

        User user = findUserById(userId);
        user.setStatus(UserStatus.INACTIVE);

        user = userRepository.save(user);
        log.info("User deactivated: {}", userId);

        return userMapper.toResponse(user);
    }

    /**
     * Delete user (hard delete)
     */
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Deleting user: {}", userId);

        User user = findUserById(userId);

        if (user.getUserType() == UserType.SUPER_ADMIN) {
            throw new BadRequestException("Cannot delete SUPER_ADMIN users");
        }

        // Type-specific records will be deleted via CASCADE
        userRepository.delete(user);
        log.info("User deleted: {}", userId);
    }

    /**
     * Reset login attempts
     */
    @Transactional
    public UserResponse resetLoginAttempts(UUID userId) {
        log.info("Resetting login attempts for user: {}", userId);

        User user = findUserById(userId);
        user.setLoginAttempts(0);

        user = userRepository.save(user);
        log.info("Login attempts reset for user: {}", userId);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);

        User user = findUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Set new password
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }

    // ==================== Helper Methods ====================

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private void createTypeSpecificRecord(UUID userId, UserType userType, UUID trackId) {
        switch (userType) {
            case SUPERVISOR -> {
                Supervisor supervisor = new Supervisor(userId);
                supervisorRepository.save(supervisor);
                log.info("Supervisor record created for user: {}", userId);
            }
            case INTERN -> {
                Intern intern = new Intern(userId, trackId);
                internRepository.save(intern);
                log.info("Intern record created for user: {} with track: {}", userId, trackId);
            }
            case SUPER_ADMIN -> {
                // No additional record needed
                log.info("SUPER_ADMIN user created: {}", userId);
            }
        }
    }

    private void validateTrackExists(UUID trackId) {
        // This would check if track exists in track table
        // For now, we'll assume it exists
        // trackRepository.findById(trackId)
        //     .orElseThrow(() -> new NotFoundException("Track not found"));
    }
}
