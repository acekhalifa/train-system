package com.esl.academy.api.app_config;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "app_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "app_config_id")
    private String appConfigId;

    @Column(name = "app_config_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "is_available_to_public", nullable = false)
    private boolean isAvailableToPublic;

    @Column(name = "is_check", nullable = false)
    private boolean isCheck;

    @Column(name = "possible_values", columnDefinition = "TEXT")
    private String possibleValues;

    @Column
    private String description;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", insertable = false)
    private OffsetDateTime updatedAt;

    @LastModifiedBy
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modified_by", insertable = false, columnDefinition = "json")
    private String modifiedBy;

    public AppConfig(String appConfigId) {
        this.appConfigId = appConfigId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppConfig appConfig = (AppConfig) o;
        return isAvailableToPublic == appConfig.isAvailableToPublic && isCheck == appConfig.isCheck && Objects.equals(appConfigId, appConfig.appConfigId) && Objects.equals(value, appConfig.value) && Objects.equals(possibleValues, appConfig.possibleValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appConfigId, value, isAvailableToPublic, isCheck, possibleValues);
    }

    @Override
    public String toString() {
        return "AppConfig{" +
            "id='" + appConfigId + '\'' +
            ", value='" + value + '\'' +
            ", isAvailableToPublic=" + isAvailableToPublic +
            ", isCheck=" + isCheck +
            ", possibleValues='" + possibleValues + '\'' +
            '}';
    }
}
