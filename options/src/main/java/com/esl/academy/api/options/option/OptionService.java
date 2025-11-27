package com.esl.academy.api.options.option;

import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    public List<OptionDto> getOptionsByType(UUID optionTypeId) {
        return optionMapper
            .toOptionDtoList(optionRepository.findByOptionTypeId(optionTypeId));
    }

    public OptionDto getOptionByName(String name){
        return
            optionMapper.
                toOptionDto(optionRepository.findByOptionName(name)
            .orElseThrow(() -> new NotFoundException("Option with name " +name+ " does not exist")));
    }
}
