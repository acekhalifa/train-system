package com.esl.academy._2025.api.user;


import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "profile_picture_link")
    private String profilePictureLink;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(passwordHash, user.passwordHash) && userType == user.userType && status == user.status && Objects.equals(profilePictureLink, user.profilePictureLink) && Objects.equals(lastLogin, user.lastLogin) && Objects.equals(loginAttempts, user.loginAttempts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, passwordHash, userType, status, profilePictureLink, lastLogin, loginAttempts);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", passwordHash='" + passwordHash + '\'' +
            ", userType=" + userType +
            ", status=" + status +
            ", profilePictureLink='" + profilePictureLink + '\'' +
            ", lastLogin=" + lastLogin +
            ", loginAttempts=" + loginAttempts +
            '}';
    }
}
