package com.esl.academy.api.learning_resource;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class LearningResourceSpecification {

    public static Specification<LearningResource> byTrackId(UUID trackId) {
        return (root, query, cb) ->
            cb.equal(root.get("trackId"), trackId);
    }

    public static Specification<LearningResource> byMonthId(UUID monthId) {
        if (monthId == null) return null;

        return (root, query, cb) ->
            cb.equal(root.get("monthId"), monthId);
    }

    public static Specification<LearningResource> byWeekId(UUID weekId) {
        if (weekId == null) return null;

        return (root, query, cb) ->
            cb.equal(root.get("weekId"), weekId);
    }

    public static Specification<LearningResource> byResourceTitle(String title) {
        if (title == null || title.isBlank()) return null;

        return (root, query, cb) ->
            cb.like(cb.lower(root.get("resourceTitle")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<LearningResource> byDescription(String description) {
        if (description == null || description.isBlank()) return null;

        return (root, query, cb) ->
            cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}
