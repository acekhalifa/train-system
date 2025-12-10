package com.esl.academy.api.integration.tests.option;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.options.option_type.OptionTypeDto;
import com.esl.academy.api.options.option_type.OptionTypeRepository;
import com.esl.academy.api.options.option_type.OptionTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionTypeServiceTest extends BaseIntegrationTest {
    @Autowired
    private OptionTypeService optionTypeService;
    @Autowired
    private OptionTypeRepository optionTypeRepository;

    @Test
    void getAllOptionTypes_withExistingTypes_shouldReturnAllTypes() {
        List<OptionTypeDto> result = optionTypeService.getAllOptionTypes();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void getOptionTypeById_withNonExistentId_shouldThrowNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            optionTypeService.getOptionTypeById(nonExistentId));

        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }
}
