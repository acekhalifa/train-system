package com.esl.academy.api.integration.tests.option;

import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.options.option.OptionDto;
import com.esl.academy.api.options.option.OptionRepository;
import com.esl.academy.api.options.option.OptionService;
import com.esl.academy.api.options.option_type.OptionType;
import com.esl.academy.api.options.option_type.OptionTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionServiceTest extends BaseIntegrationTest {

    @Autowired
    private OptionService optionService;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private OptionTypeRepository optionTypeRepository;
    private OptionType weekType;

    @BeforeEach
    void setUp() {
        weekType = optionTypeRepository.findByName("weeek")
            .orElseThrow(() -> new AssertionError("Migration 'week' type should exist"));
    }

    @Test
    void getOptionsByType_withMigrationWeekType_shouldReturnOptions() {
        List<OptionDto> result = optionService.getOptionsByType(weekType.getOptionTypeId());

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.optionTypeId().equals(weekType.getOptionTypeId())));
        assertTrue(result.stream().anyMatch(dto -> dto.name().equals("1")));
        assertTrue(result.stream().anyMatch(dto -> dto.name().equals("2")));
        assertTrue(result.stream().anyMatch(dto -> dto.name().equals("3")));
        assertTrue(result.stream().anyMatch(dto -> dto.name().equals("4")));
        assertTrue(result.stream().anyMatch(dto -> dto.name().equals("5")));
    }

    @Test
    void getOptionsByType_withNonExistentTypeId_shouldReturnEmptyList() {
        UUID nonExistentId = UUID.randomUUID();
        List<OptionDto> result = optionService.getOptionsByType(nonExistentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
