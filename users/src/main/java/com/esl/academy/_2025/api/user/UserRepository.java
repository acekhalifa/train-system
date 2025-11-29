package com.esl.academy._2025.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByUserType(UserType userType);

    List<User> findByStatus(UserStatus status);

    List<User> findByUserTypeAndStatus(UserType userType, UserStatus status);

    @Query("SELECT u FROM User u WHERE u.userType = :userType ORDER BY u.dateCreated DESC")
    List<User> findAllByUserTypeOrderByDateCreatedDesc(UserType userType);
}
