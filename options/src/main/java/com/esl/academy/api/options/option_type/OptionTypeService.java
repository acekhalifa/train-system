package com.esl.academy.api.options.option_type;

import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.esl.academy.api.options.option_type.OptionTypeMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class OptionTypeService {

    private final OptionTypeRepository optionTypeRepository;

    public List<OptionTypeDto> getAllOptionTypes() {
        return INSTANCE.map(optionTypeRepository.findAll());
    }

    public OptionTypeDto getOptionTypeById(UUID id) {
        final var optionType = optionTypeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("OptionType not found"));
        return INSTANCE.map(optionType);
    }

    public Optional<OptionType> findByName(String name) {
        return optionTypeRepository.findByName(name);
    }

}
