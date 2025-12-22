package com.esl.academy.api.submission.link;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {

    List<Link> findByObjectIdAndLinkType(UUID objectId, LinkType linkType);
}
