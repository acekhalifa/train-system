package com.esl.academy.api.assessment;

import com.esl.academy.api.core.audit.AuditBase;
import com.esl.academy.api.learning_resource.LearningResource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assessment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Assessment extends AuditBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;

    @OneToOne
    @JoinColumn(name = "learning_resource_id", insertable = false, updatable = false)
    private LearningResource learningResource;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline", nullable = false)
    private OffsetDateTime deadline;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "published_status")
    private PublishedStatus publishedStatus;

    public Assessment(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Assessment that = (Assessment) o;
        return getAssessmentId() != null && Objects.equals(getAssessmentId(), that.getAssessmentId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Assessment{" +
            "assessmentId=" + assessmentId +
            ", description='" + description + '\'' +
            ", deadline=" + deadline +
            ", isDeleted=" + isDeleted +
            ", publishedStatus=" + publishedStatus +
            super.toString() +
            '}';
    }
}
