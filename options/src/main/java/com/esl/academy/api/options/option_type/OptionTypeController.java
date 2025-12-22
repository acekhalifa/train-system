package com.esl.academy.api.options.option_type;

import com.esl.academy.api.options.option.OptionDto;
import com.esl.academy.api.options.option.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@Tag(name = "Option Types")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/option-types")
@SecurityRequirement(name = "Authorization")
public class OptionTypeController {
    private final OptionService optionService;
    private final OptionTypeService optionTypeService;

    @Operation(summary = "Get all Option Types")
    @GetMapping
    public List<OptionTypeDto> getAllOptionTypes() {
        return optionTypeService.getAllOptionTypes();
    }

    @Operation(summary = "Get Option Type by ID")
    @GetMapping("{optionTypeId}")
    public OptionTypeDto getOptionTypeById(
        @Parameter(description = "ID of the Option Type")
        @PathVariable UUID optionTypeId) {
        return optionTypeService.getOptionTypeById(optionTypeId);
    }

    @Operation(summary = "Get Options by Type ID")
    @GetMapping("{optionTypeId}/options")
    public List<OptionDto> getOptionsByTypeId(
        @PathVariable UUID optionTypeId) {
        return optionService.getOptionsByType(optionTypeId);
    }
}
