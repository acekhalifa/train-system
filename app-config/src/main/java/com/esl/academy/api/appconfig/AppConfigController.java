package com.esl.academy.api.appconfig;

import com.esl.academy.api.core.constants.AppConfigId;
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
import static com.esl.academy.api.appconfig.AppConfigDTO.*;

@Tag(name = "App Config")
@RequestMapping("api/v1/app-configs")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@RestController
public class AppConfigController {
    private final AppConfigService service;

    @Operation(summary = "Get all App Configs")
    @GetMapping
    public List<AppConfigDTO> getAll() {
        return service.getAllConfigs();
    }

    @Operation(summary = "Get App Config by ID")
    @GetMapping("{configId}")
    public AppConfig getById(@PathVariable AppConfigId configId) {
        return service.getAppConfigById(configId);
    }

    @Operation(summary = "Update App Config by ID")
    @PatchMapping("{configId}")
    public AppConfigDTO update(
        @PathVariable String configId,
        @RequestBody @Valid UpdateAppConfigDTO request) {
        return service.updateAppConfig(configId, request);
    }
}
