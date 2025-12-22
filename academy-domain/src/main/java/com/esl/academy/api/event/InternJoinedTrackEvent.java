package com.esl.academy.api.event;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InternJoinedTrackEvent(@NotNull UUID trackId) {}
