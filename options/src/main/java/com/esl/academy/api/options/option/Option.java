package com.esl.academy.api.options.option;

import com.esl.academy.api.core.audit.AuditBase;
import com.esl.academy.api.options.option_type.OptionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Option extends AuditBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "option_id")
    private UUID optionId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_type_id", nullable = false)
    @JsonIgnore
    private OptionType optionType;

    public Option(UUID id) {
        this.optionId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return Objects.equals(optionId, option.optionId) && Objects.equals(name, option.name) && Objects.equals(description, option.description) && Objects.equals(optionType, option.optionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionId, name, description, optionType);
    }

    @Override
    public String toString() {
        return "Option{" +
            "optionId=" + optionId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", optionType=" + optionType +
            '}';
    }
}
