package com.esl.academy.api.submission.link;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "link")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID linkId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private UUID objectId;

    @Column(name = "object_type", nullable = false, columnDefinition = "link_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private LinkType linkType;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Link link)) return false;
        return Objects.equals(getLinkId(), link.getLinkId()) && Objects.equals(getUrl(), link.getUrl()) && Objects.equals(getTitle(), link.getTitle()) && Objects.equals(getObjectId(), link.getObjectId()) && getLinkType() == link.getLinkType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLinkId(), getUrl(), getTitle(), getObjectId(), getLinkType());
    }

    @Override
    public String toString() {
        return "Link{" +
            "id=" + linkId +
            ", url='" + url + '\'' +
            ", title='" + title + '\'' +
            ", objectId=" + objectId +
            ", linkType=" + linkType +
            '}';
    }
}
