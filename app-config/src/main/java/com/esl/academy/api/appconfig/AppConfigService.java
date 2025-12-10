package com.esl.academy.api.appconfig;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.esl.academy.api.appconfig.AppConfigDTO.*;
import static com.esl.academy.api.appconfig.AppConfigMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private final AppConfigRepository repository;

    public List<AppConfigDTO> getAllConfigs() {
        return INSTANCE.toDtoList(repository.findAll());
    }

    public AppConfig getAppConfigById(AppConfigId configId){
        return repository.findById(configId.name())
            .orElseThrow(() -> new NotFoundException("app config with  " + configId.name() + " not found"));
    }

    public AppConfigDTO updateAppConfig(String appConfigId, UpdateAppConfigDTO appConfig) {
        AppConfig existingConfig = repository.findById(appConfigId)
            .orElseThrow(() -> new NotFoundException("app config with  " + appConfigId + " not found"));
        existingConfig.setValue(appConfig.value());
        existingConfig.setDescription(appConfig.description());
        return INSTANCE.toDto(repository.save(existingConfig));
    }

}
