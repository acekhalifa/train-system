package com.esl.academy.api.core.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class HalfAuditBase {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateCreated;

    @CreatedBy
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "created_by", updatable = false, columnDefinition = "json")
    private String createdBy;
}
