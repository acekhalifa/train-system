package com.esl.academy.api.submission;

import com.esl.academy.api.assessment.Assessment;
import com.esl.academy.api.Document;
import com.esl.academy.api.core.audit.AuditBase;
import com.esl.academy.api.user.Intern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Submission extends AuditBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @OneToMany
    @JoinTable(
        name = "submission_document",
        joinColumns = @JoinColumn(name = "submission_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documents = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "submission_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SubmissionStatus submissionStatus;

    @Column(nullable = false, columnDefinition = "grading_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GradingStatus gradingStatus;

    @Column
    private boolean isDeleted;

    @Column
    private String feedback;

    @Column
    private String submissionNote;

    @Column(nullable = false)
    private int score;

    public Submission(UUID submissionId) {
        this.submissionId = submissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Submission that)) return false;
        return isDeleted == that.isDeleted
            && score == that.score
            && Objects.equals(submissionId, that.submissionId)
            && Objects.equals(intern, that.intern)
            && Objects.equals(assessment, that.assessment)
            && submissionStatus == that.submissionStatus
            && gradingStatus == that.gradingStatus
            && Objects.equals(feedback, that.feedback)
            && Objects.equals(submissionNote, that.submissionNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            submissionId, intern, assessment, submissionStatus,
            gradingStatus, isDeleted, feedback, submissionNote, score
        );
    }

    @Override
    public String toString() {
        return "Submission{" +
            "submissionId=" + submissionId +
            ", intern=" + intern +
            ", assessment=" + assessment +
            ", documents=" + documents +
            ", submissionStatus=" + submissionStatus +
            ", gradingStatus=" + gradingStatus +
            ", isDeleted=" + isDeleted +
            ", feedback='" + feedback + '\'' +
            ", submissionNote='" + submissionNote + '\'' +
            ", score=" + score +
            '}';
    }

}
