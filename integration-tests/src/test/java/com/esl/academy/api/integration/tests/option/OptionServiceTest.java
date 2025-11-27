package com.esl.academy.api.integration.tests.option;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.options.option.OptionDto;
import com.esl.academy.api.options.option.OptionRepository;
import com.esl.academy.api.options.option.OptionService;
import com.esl.academy.api.options.optiontype.OptionType;
import com.esl.academy.api.options.optiontype.OptionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class OptionServiceTest extends BaseIntegrationTest {

    private final OptionService optionService;
    private final OptionRepository optionRepository;
    private final OptionTypeRepository optionTypeRepository;
    private OptionType weekType;

    @BeforeEach
    void setUp() {
        weekType = optionTypeRepository.findByName("week")
            .orElseThrow(() -> new AssertionError("Migration 'week' type should exist"));
    }

    @Test
    void getOptionsByType_withMigrationWeekType_shouldReturnOptions() {
        List<OptionDto> result = optionService.getOptionsByType(weekType.getId());

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.optionTypeId().equals(weekType.getId())));
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

    @Test
    void getOptionByName_withMigrationWeekOption_shouldReturnWeekOption() {
        OptionDto result = optionService.getOptionByName("1");

        assertNotNull(result);
        assertEquals("1", result.name());
        assertEquals(weekType.getId(), result.optionTypeId());
        assertEquals("week", result.optionTypeName());
    }

    @Test
    void getOptionByName_withNonExistentName_shouldThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            optionService.getOptionByName("NonExistentOption"));

        assertTrue(exception.getMessage().contains("NonExistentOption"));
        assertTrue(exception.getMessage().contains("does not exist"));
    }
}
