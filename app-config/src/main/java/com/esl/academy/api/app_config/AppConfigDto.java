package com.esl.academy.api.app_config;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record AppConfigDto(String appConfigId,
                           String value,
                           String possibleValues,
                           String description,
                           OffsetDateTime updatedAt,
                           String modifiedBy) {

    public record UpdateAppConfigDto(@Size(min = 1, max = 256) String value, String description) {}

}
