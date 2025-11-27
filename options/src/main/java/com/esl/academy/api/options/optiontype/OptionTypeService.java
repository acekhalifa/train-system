package com.esl.academy.api.options.optiontype;

import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionTypeService {
    private final OptionTypeRepository optionTypeRepository;
    private final OptionTypeMapper optionTypeMapper;

    public List<OptionTypeDto> getAllOptionTypes() {
        return optionTypeMapper.
            toOptionTypeDtoList(optionTypeRepository.findAll());
    }

    public OptionTypeDto getOptionTypeById(UUID id) {
        OptionType optionType = optionTypeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("OptionType not found with id: " + id));
        return optionTypeMapper.toOptionTypeDto(optionType);
    }

}
