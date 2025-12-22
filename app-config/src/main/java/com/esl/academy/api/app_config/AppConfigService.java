package com.esl.academy.api.app_config;

import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import static com.esl.academy.api.app_config.AppConfigDto.*;
import static com.esl.academy.api.app_config.AppConfigMapper.INSTANCE;
import static java.time.OffsetDateTime.now;

@Service
@RequiredArgsConstructor
public class AppConfigService {

    private final AppConfigRepository repository;

    public List<AppConfigDto> getAllConfigs() {
        return INSTANCE.map(repository.findAll());
    }

    public Optional<AppConfigDto> getAppConfigById(AppConfigId configId){
        return repository.findById(configId.name())
            .map(INSTANCE::map);
    }

    public AppConfigDto updateAppConfig(String appConfigId, UpdateAppConfigDto appConfig) {
        AppConfig existingConfig = repository.findById(appConfigId)
            .orElseThrow(() -> new NotFoundException("app config with  " + appConfigId + " not found"));
        existingConfig.setValue(appConfig.value());
        existingConfig.setDescription(appConfig.description());
        existingConfig.setUpdatedAt(now());
        return INSTANCE.map(repository.save(existingConfig));
    }

}
