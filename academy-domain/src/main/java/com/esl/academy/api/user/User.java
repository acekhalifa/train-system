package com.esl.academy.api.user;

import com.esl.academy.api.core.audit.AuditBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, columnDefinition = "user_type")
    private UserType userType;

    @Column
    private UUID trackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "user_status")
    private UserStatus status;

    @Column(name = "profile_picture_link")
    private String profilePictureLink;

    @Column(name = "last_login_date")
    private OffsetDateTime lastLoginDate;

    @Column(name = "login_attempts")
    private short loginAttempts;

    @OneToOne(mappedBy = "user")
    private Intern intern;

    private String address;

    private Long phoneNumber;


    public User(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        String name = String.format("%s %s",
            Objects.toString(firstName, ""),
            Objects.toString(lastName, "")).trim();
        return StringUtils.isBlank(name) ? email : name;
    }

    public String stringify() {
        return stringifyUserMaker().toString();
    }

    private ObjectNode stringifyUserMaker() {
        return new ObjectMapper().createObjectNode()
            .put("userId", userId.toString())
            .put("email", email)
            .put("name", getName());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getUserId() != null && Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", passwordHash='" + password + '\'' +
            ", userType=" + userType +
            ", status=" + status +
            ", profilePictureLink='" + profilePictureLink + '\'' +
            ", lastLogin=" + lastLoginDate +
            ", loginAttempts=" + loginAttempts +
            '}';
    }
}
