package com.esl.academy.api.user;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.intern.InternRepository;
import com.esl.academy.api.intern.InternStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.esl.academy.api.user.UserDto.UpdateUserDto;
import static com.esl.academy.api.user.UserDto.UpdateUserTypeDto;
import static com.esl.academy.api.user.UserDto.UpdateUserStatusDto;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InternRepository internRepository;

    public UserDto updateUser(UUID userId, UpdateUserDto dto) {
        User user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
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

        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    public UserDto changeUserRole(UUID userId, UpdateUserTypeDto dto) {
        User user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setUserType(dto.userType());
        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    public UserDto changeUserStatus(UUID userId, UpdateUserStatusDto dto) {
        User user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setStatus(dto.status());
        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    public Page<UserDto> getAllSupervisors(Pageable pageable) {
        return userRepository.findByUserType(UserType.SUPERVISOR, pageable)
            .map(UserMapper.INSTANCE::toDto);
    }

    public Page<UserDto> getAllSuperAdmins(Pageable pageable) {
        return userRepository.findByUserType(UserType.SUPER_ADMIN, pageable)
            .map(UserMapper.INSTANCE::toDto);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.ACTIVE, pageable)
            .map(UserMapper.INSTANCE::toDto);
    }

    public UserDto getUserById(UUID userId) {
        User user =  userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        return UserMapper.INSTANCE.toDto(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        user.setStatus(UserStatus.INACTIVE);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        internRepository.findById(userId).ifPresent(intern -> {
            intern.setInternStatus(InternStatus.DISCONTINUED);
            intern.setVersion(intern.getVersion() + 1); // optional if using @Version
            internRepository.save(intern);
        });
    }

    public Page<UserDto> searchSupervisors(
        UUID assignedTrackId,
        UserStatus status,
        OffsetDateTime createdAt,
        String name,
        Pageable pageable
    ) {
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
            .map(UserMapper.INSTANCE::toDto);
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
            .map(UserMapper.INSTANCE::toDto);
    }

}
