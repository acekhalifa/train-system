package com.esl.academy.api.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail (String email);

    Optional<User> findByUserId(UUID userId);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    Optional<User> findByUserIdAndStatus(UUID userId, UserStatus status);

    Page<User> findByUserType(UserType userType, Pageable pageable);
}
