package com.esl.academy.api.appconfig;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Table(name = "app_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig extends AuditBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column
    private String description;

    public AppConfig(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppConfig appConfig = (AppConfig) o;
        return isAvailableToPublic == appConfig.isAvailableToPublic && isCheck == appConfig.isCheck && Objects.equals(id, appConfig.id) && Objects.equals(value, appConfig.value) && Objects.equals(possibleValues, appConfig.possibleValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, isAvailableToPublic, isCheck, possibleValues);
    }

    @Override
    public String toString() {
        return "AppConfig{" +
            "id='" + id + '\'' +
            ", value='" + value + '\'' +
            ", isAvailableToPublic=" + isAvailableToPublic +
            ", isCheck=" + isCheck +
            ", possibleValues='" + possibleValues + '\'' +
            '}';
    }
}
