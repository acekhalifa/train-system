package com.esl.academy.api.options.option_type;

import java.util.UUID;

public record OptionTypeDto(
    UUID optionTypeId,
    String name
) {}
