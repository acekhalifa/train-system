package com.esl.academy._2025.api.user;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "intern")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intern  {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "track_id", nullable = false)
    private UUID trackId;
}
