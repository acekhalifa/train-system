package com.esl.academy.api.integration.tests.appconfig;

import com.esl.academy.api.appconfig.AppConfig;
import com.esl.academy.api.appconfig.AppConfigRepository;
import com.esl.academy.api.appconfig.AppConfigService;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppConfigServiceTest extends BaseIntegrationTest {

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private AppConfigRepository appConfigRepository;

    private final Map<String, Object> testAuditInfo = Map.of("user", "test_admin");

    @BeforeEach
    void setUp() {
        appConfigRepository.deleteAll();
    }

    @Test
    void getConfigs_shouldReturnAllConfigs() {
        List<AppConfig> result = appConfigService.getAllConfigs();
        assertEquals(2, result.size());
    }

    @Test
    void getConfig_shouldReturnSingleConfig(){

    }
}
