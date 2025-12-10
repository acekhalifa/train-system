package com.esl.academy.api.learning_resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface LearningResourceRepository extends JpaRepository<LearningResource, UUID>, JpaSpecificationExecutor<LearningResource> {

    Page<LearningResource> findByTrackIdAndIsDeletedFalse(UUID trackId, Pageable pageable);

    Optional<LearningResource> findByLearningResourceIdAndIsDeletedFalse(UUID learningResourceId);
}
