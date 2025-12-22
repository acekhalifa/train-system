package com.esl.academy.api.app_config;

import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "App Config")
@RequestMapping("api/v1/app-configs")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@RestController
public class AppConfigController {

    private final AppConfigService appConfigService;

    @Operation(summary = "Get all App Configs")
    @GetMapping
    public List<AppConfigDto> getAll() {
        return appConfigService.getAllConfigs();
    }

    @Operation(summary = "Get App Config by ID")
    @GetMapping("{configId}")
    public AppConfigDto getById(@PathVariable AppConfigId configId) {
        return appConfigService.getAppConfigById(configId)
            .orElseThrow(() -> new NotFoundException("App Config not found"));
    }

    @Operation(summary = "Update App Config by ID")
    @PatchMapping("{configId}")
    public AppConfigDto update(
        @PathVariable String configId,
        @RequestBody @Valid AppConfigDto.UpdateAppConfigDto request) {
        return appConfigService.updateAppConfig(configId, request);
    }
}
