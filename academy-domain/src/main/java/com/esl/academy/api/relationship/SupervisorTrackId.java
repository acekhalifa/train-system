package com.esl.academy.api.relationship;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupervisorTrackId implements Serializable {

    private UUID userId;

    private UUID trackId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SupervisorTrackId that = (SupervisorTrackId) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
            && getTrackId() != null && Objects.equals(getTrackId(), that.getTrackId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(userId, trackId);
    }

    @Override
    public String toString() {
        return "SupervisorTrackId{" +
            "userId=" + userId +
            ", trackId=" + trackId +
            '}';
    }
}
