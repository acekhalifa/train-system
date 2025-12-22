package com.esl.academy.api.submission.event;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmissionCreatedEvent(@NotNull String internName, @NotNull String trackName) {}
