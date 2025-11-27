package com.esl.academy.api.core.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditBase {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateCreated;

    @CreatedBy
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "created_by", updatable = false, columnDefinition = "json")
    private String createdBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified", insertable  = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateModified;

    @LastModifiedBy
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modified_by", insertable = false, columnDefinition = "json")
    private String modifiedBy;

    @PreUpdate
    public void preUpdate() {
        this.dateModified = OffsetDateTime.now(ZoneId.of("Africa/Lagos"));
    }

    public String getCreatedByUserId() {
        final var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(createdBy).get("userId").asText();
        } catch (JsonProcessingException | IllegalArgumentException e) {
            System.out.println("Failed to deserialize created by: " + e.getMessage());
            return null;
        }
    }
}
