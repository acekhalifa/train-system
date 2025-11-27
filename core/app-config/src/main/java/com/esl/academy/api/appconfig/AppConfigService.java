package com.esl.academy.api.appconfig;

import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppConfigService {

    private final AppConfigRepository repository;

    public List<AppConfig> getAllConfigs() {
        return repository.findAll();
    }

    public AppConfig getAppConfigById(AppConfigId configId){
        return repository.findById(configId.name())
            .orElseThrow(() -> new NotFoundException("app config with  " + configId.name() + " not found"));
    }

}
