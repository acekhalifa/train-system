package com.esl.academy.api.integration.tests.appconfig;

import com.esl.academy.api.app_config.AppConfigDto;
import com.esl.academy.api.app_config.AppConfigRepository;
import com.esl.academy.api.app_config.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AppConfigServiceTest extends BaseIntegrationTest {

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private AppConfigRepository appConfigRepository;

    @Test
    void getAllConfigs_withExistingConfigs_shouldReturnAllConfigs() {
        List<AppConfigDto> result = appConfigService.getAllConfigs();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream()
            .anyMatch(c -> ("MAX_LOGIN_ATTEMPTS").equals(c.appConfigId())));
        assertTrue(result.stream()
            .anyMatch(c -> ("FILE_BYTE_UPLOAD_LIMIT").equals(c.appConfigId())));
    }

    @Test
    void getAppConfigById_withValidConfigId_shouldReturnConfig() {
        String configValue = "5";
        var result = appConfigService.getAppConfigById(AppConfigId.MAX_LOGIN_ATTEMPTS)
            .orElseThrow();

        assertNotNull(result);
        assertEquals(AppConfigId.MAX_LOGIN_ATTEMPTS.name(), result.appConfigId());
        assertEquals(configValue, result.value());
    }

}
