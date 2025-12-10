package com.esl.academy.api;

import com.esl.academy.api.core.audit.AuditBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Document extends AuditBase implements Serializable {
    @Id
    @Column(nullable = false)
    @Builder.Default
    private UUID documentId = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String documentPath;

    @Column(nullable = false)
    private Long byteSize;

    @Column(nullable = false)
    private Boolean attachment;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String extensionGroup;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Document(UUID documentId) {
        this.documentId = documentId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Document document = (Document) o;
        return getDocumentId() != null && Objects.equals(getDocumentId(), document.getDocumentId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Document{" +
            "certificateId=" + documentId +
            ", name='" + name + '\'' +
            ", fileType=" + fileType +
            ", documentPath='" + documentPath + '\'' +
            ", byteSize=" + byteSize +
            ", attachment=" + attachment +
            ", extension='" + extension + '\'' +
            ", extensionGroup='" + extensionGroup + '\'' +
            ", isDeleted=" + isDeleted +
            super.toString() +
            '}';
    }
}
