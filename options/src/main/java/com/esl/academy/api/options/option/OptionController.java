package com.esl.academy.api.options.option;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import static com.esl.academy.api.options.option.OptionDto.AddUpdateOptionDto;

@Tag(name = "Options")
@RestController
@RequestMapping("api/v1/options")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class OptionController {
    private final OptionService optionService;

    @Operation(summary = "Add a new Option")
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public OptionDto addOption(@RequestBody @Valid AddUpdateOptionDto request) {
        return optionService.createOption(request);
    }

    @Operation(summary = "Get an Option by ID")
    @GetMapping("{optionId}")
    public OptionDto getOptionById(@PathVariable UUID optionId) {
       return  optionService.getOptionById(optionId);
    }

    @Operation(summary = "Update an Option by ID")
    @PatchMapping("{optionId}")
    public OptionDto updateOption(
        @PathVariable UUID optionId,
        @RequestBody @Valid AddUpdateOptionDto request) {
        return optionService.updateOption(optionId, request);
    }

    @Operation(summary = "Delete an Option by ID")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("{optionId}")
    public void deleteOption(@PathVariable UUID optionId) {
        optionService.deleteOption(optionId);
    }
}
