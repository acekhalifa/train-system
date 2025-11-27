package com.esl.academy.api.options.option;

import java.util.UUID;

public record OptionDto(UUID id,
                        String name,
                        UUID optionTypeId,
                        String optionTypeName) {}
