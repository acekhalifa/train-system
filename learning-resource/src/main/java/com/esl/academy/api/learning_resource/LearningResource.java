package com.esl.academy.api.learning_resource;

import com.esl.academy.api.core.audit.AuditBase;
import com.esl.academy.api.options.option.Option;
import com.esl.academy.api.track.Track;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "learning_resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LearningResource extends AuditBase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "learning_resource_id", nullable = false)
    private UUID learningResourceId;

    @Column(nullable = false)
    private UUID trackId;

    @Column(nullable = false)
    private UUID monthId;

    @Column(nullable = false)
    private UUID weekId;

    @Column(nullable = false, length = 100)
    private String resourceTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isDeleted;

    public LearningResource(UUID learningResourceId) {
        this.learningResourceId = learningResourceId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LearningResource that = (LearningResource) o;
        return getLearningResourceId() != null && Objects.equals(getLearningResourceId(), that.getLearningResourceId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "LearningResource{" +
            "learningResourceId=" + learningResourceId +
            ", trackId=" + trackId +
            ", monthId=" + monthId +
            ", weekId=" + weekId +
            ", resourceTitle='" + resourceTitle + '\'' +
            ", description='" + description + '\'' +
            ", isDeleted=" + isDeleted +
            '}';
    }
}
