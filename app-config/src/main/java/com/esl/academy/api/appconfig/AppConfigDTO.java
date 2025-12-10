package com.esl.academy.api.appconfig;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record AppConfigDTO(
    String appConfigId,
    String value,
    String possibleValues,
    OffsetDateTime dateCreated,
    String description,
    String createdBy,
    OffsetDateTime dateModified,
    String modifiedBy
) {
    public record UpdateAppConfigDTO(
        @Size(min = 1, max = 256)
        String value,
        String description) {
    }
}
