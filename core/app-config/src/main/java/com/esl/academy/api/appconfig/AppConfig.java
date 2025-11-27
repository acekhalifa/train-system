package com.esl.academy.api.appconfig;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "app_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig extends AuditBase {

    @Id
    @Column(name = "app_config_id")
    private String id;

    @Column(name = "app_config_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "is_available_to_public", nullable = false)
    private boolean isAvailableToPublic;

    @Column(name = "is_check", nullable = false)
    private boolean isCheck;

    @Column(name = "possible_values", columnDefinition = "TEXT")
    private String possibleValues;

}
