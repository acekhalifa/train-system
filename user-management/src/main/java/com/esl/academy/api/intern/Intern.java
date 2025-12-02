package com.esl.academy.api.intern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.util.UUID;

@Builder
@Entity
@Table(name = "intern")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Intern {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "track_id", nullable = false)
    @ManyToOne
    private UUID trackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "intern_status")
    InternStatus internStatus;

    public Intern(UUID userId){
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Intern intern = (Intern) o;
        return Objects.equals(userId, intern.userId) && Objects.equals(trackId, intern.trackId) && internStatus == intern.internStatus;
    }

    @Override
    public String toString() {
        return "Intern{" +
            "userId=" + userId +
            ", trackId=" + trackId +
            ", internStatus=" + internStatus +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, trackId, internStatus);
    }
}
