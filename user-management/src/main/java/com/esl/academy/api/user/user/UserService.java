package com.esl.academy.api.user.user;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.UserType;
import com.esl.academy.api.user.intern.InternRepository;
import com.esl.academy.api.user.InternStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.esl.academy.api.user.user.UserDto.UpdateUserDto;
import static com.esl.academy.api.user.user.UserDto.UpdateUserTypeDto;
import static com.esl.academy.api.user.user.UserDto.UpdateUserStatusDto;
import static com.esl.academy.api.user.user.UserMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InternRepository internRepository;

    public UserDto upsertUser(User user) {
        return INSTANCE.map(userRepository.save(user));
    }

    public UserDto updateUser(UUID userId, UpdateUserDto dto) {
        final var user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }
        if (dto.email() != null) {
            user.setEmail(dto.email());
        }
        if (dto.profilePictureLink() != null) {
            user.setProfilePictureLink(dto.profilePictureLink());
        }

        return INSTANCE.map(userRepository.save(user));
    }

    public UserDto changeUserRole(UUID userId, UpdateUserTypeDto dto) {
        final var user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setUserType(dto.userType());
        return INSTANCE.map(userRepository.save(user));
    }

    public UserDto changeUserStatus(UUID userId, UpdateUserStatusDto dto) {
        final var user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setStatus(dto.status());
        return INSTANCE.map(userRepository.save(user));
    }

    public Optional<UserDto> getUser(UUID userId) {
        return userRepository.findById(userId).map(INSTANCE::map);
    }

    public Page<UserDto> getAllSupervisors(Pageable pageable) {
        return userRepository.findByUserType(UserType.SUPERVISOR, pageable)
            .map(INSTANCE::map);
    }

    public Page<UserDto> getAllSuperAdmins(Pageable pageable) {
        return userRepository.findByUserType(UserType.SUPER_ADMIN, pageable)
            .map(INSTANCE::map);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.ACTIVE, pageable)
            .map(INSTANCE::map);
    }

    public UserDto getUserById(UUID userId) {
        final var user =  userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        return INSTANCE.map(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public void deleteUser(UUID userId) {
        final var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setStatus(UserStatus.INACTIVE);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        internRepository.findById(userId).ifPresent(intern -> {
            intern.setInternStatus(InternStatus.DISCONTINUED);
            internRepository.save(intern);
        });
    }

    public Page<UserDto> searchSupervisors(UUID assignedTrackId,
                                           UserStatus status,
                                           OffsetDateTime createdAt,
                                           String name,
                                           Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> null;

        if (assignedTrackId != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("assignedTrackId"), assignedTrackId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), status));
        }

        if (createdAt != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("createdAt"), createdAt));
        }

        if (name != null && !name.isBlank()) {
            String like = "%" + name.toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like)
                )
            );
        }

        return userRepository.findAll(spec, pageable)
            .map(INSTANCE::map);
    }

    public Page<UserDto> searchSuperAdmins(
        UserStatus status,
        OffsetDateTime createdAt,
        String name,
        String email,
        Pageable pageable
    ) {
        Specification<User> spec = (root, query, cb) -> null;

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), status));
        }

        if (createdAt != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("createdAt"), createdAt));
        }

        if (name != null && !name.isBlank()) {
            String like = "%" + name.toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like)
                )
            );
        }

        if (email != null && !email.isBlank()) {
            String like = "%" + email.toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("email")), like)
            );
        }

        return userRepository.findAll(spec, pageable)
            .map(INSTANCE::map);
    }

}
