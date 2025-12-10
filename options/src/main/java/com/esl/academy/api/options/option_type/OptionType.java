package com.esl.academy.api.options.option_type;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "option_type")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OptionType extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "option_type_id")
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OptionType that = (OptionType) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "OptionType{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
