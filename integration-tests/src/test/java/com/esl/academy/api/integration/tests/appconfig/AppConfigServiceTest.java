package com.esl.academy.api.integration.tests.appconfig;

import com.esl.academy.api.appconfig.AppConfig;
import com.esl.academy.api.appconfig.AppConfigRepository;
import com.esl.academy.api.appconfig.AppConfigService;
import com.esl.academy.api.core.constants.AppConfigId;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
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

    private final AppConfigService appConfigService;
    private final AppConfigRepository appConfigRepository;

    @Test
    void getConfigs_shouldReturnAllConfigs() {
        void getAllConfigs_withExistingConfigs_shouldReturnAllConfigs () {
            List<AppConfig> result = appConfigService.getAllConfigs();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream()
                .anyMatch(c -> c.getId().equals("MAX_LOGIN_ATTEMPTS")));
            assertTrue(result.stream()
                .anyMatch(c -> c.getId().equals("FILE_BYTE_UPLOAD_LIMIT")));
        }

        @Test
        void getConfig_shouldReturnSingleConfig() {

            void getAppConfigById_withValidConfigId_shouldReturnConfig () {
                String configValue = "5";
                AppConfig result = appConfigService.getAppConfigById(AppConfigId.MAX_LOGIN_ATTEMPTS);

                assertNotNull(result);
                assertEquals(AppConfigId.MAX_LOGIN_ATTEMPTS.name(), result.getId());
                assertEquals(configValue, result.getValue());
                assertFalse(result.isCheck());
            }

        }
    }
}
