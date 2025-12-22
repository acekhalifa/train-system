package com.esl.academy.api.track;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;
import org.hibernate.proxy.HibernateProxy;
import com.esl.academy.api.relationship.SupervisorTrack;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "track")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Track extends AuditBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "track_id", nullable = false)
    private UUID trackId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int duration; // duration in months

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String learningFocus;

    @Formula("(SELECT COUNT(*) FROM supervisor_track st WHERE st.track_id = track_id)")
    private int numOfSupervisors;

    @Formula("(SELECT COUNT(*) FROM intern i WHERE i.track_id = track_id)")
    private int numOfInterns;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupervisorTrack> supervisors = new HashSet<>();



    public Track(UUID trackId) {
        this.trackId = trackId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Track track = (Track) o;
        return getTrackId() != null && Objects.equals(getTrackId(), track.getTrackId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Track{" +
            "trackId=" + trackId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", duration=" + duration +
            ", isDeleted=" + isDeleted +
            ", learningFocus='" + learningFocus + '\'' +
            super.toString() +
            '}';
    }
}

