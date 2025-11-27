package com.esl.academy.api.core.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimestampAuditBase {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified", insertable  = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateModified;

    @PreUpdate
    public void preUpdate() {
        this.dateModified = OffsetDateTime.now(ZoneId.of("Africa/Lagos"));
    }
}
