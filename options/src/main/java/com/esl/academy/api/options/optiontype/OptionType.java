package com.esl.academy.api.options.optiontype;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "option_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionType extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "option_type_id")
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
}
