package com.esl.academy.api.options.option;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OptionDto(UUID optionId,
                        String name,
                        UUID optionTypeId,
                        String description,
                        OffsetDateTime createdAt,
                        OffsetDateTime readAt,
                        String createdBy,
                        String modifiedBy
                        ) {
    public record AddUpdateOptionDto(
        @NotNull
        String name,
        @NotNull
        UUID optionTypeId,
        String description
    ) {
}}
