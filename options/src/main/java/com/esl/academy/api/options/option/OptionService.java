package com.esl.academy.api.options.option;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.options.option_type.OptionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import static com.esl.academy.api.options.option.OptionDto.AddUpdateOptionDto;
import static com.esl.academy.api.options.option.OptionMapper.INSTANCE;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;
    private final OptionTypeRepository optionTypeRepository;

    public OptionDto createOption(AddUpdateOptionDto request) {
        var optionType = optionTypeRepository
            .findById(request.optionTypeId()).
            orElseThrow(() ->
                new NotFoundException("OptionType with id " + request.optionTypeId() + " not found"));
        Option option = Option.builder()
            .name(request.name())
            .optionType(optionType)
            .description(request.description())
            .build();
        return INSTANCE.toOptionDto(optionRepository.save(option));
    }

    public List<OptionDto> getOptionsByType(UUID optionTypeId) {
        return INSTANCE
            .toOptionDtoList(optionRepository.findByOptionTypeId(optionTypeId));
    }

    public OptionDto getOptionById(UUID optionId) {
        var option = optionRepository.findById(optionId)
            .orElseThrow(() -> new NotFoundException("Option with id " + optionId + " not found"));
        return INSTANCE.toOptionDto(option);
    }

    public OptionDto updateOption(UUID optionId, AddUpdateOptionDto request) {
        Option existingOption = optionRepository.findById(optionId)
            .orElseThrow(() -> new NotFoundException("Option with id " + optionId + " not found"));
        existingOption.setName(request.name());
        existingOption.setDescription(request.description());
        existingOption.setModifiedBy("system");
        return INSTANCE.toOptionDto(optionRepository.save(existingOption));
    }

    public void deleteOption(UUID optionId) {
        optionRepository.findById(optionId)
            .ifPresentOrElse(
                option -> option.setDeleted(true),
                () -> {
                    throw new NotFoundException("Option with id " + optionId + " not found");
                }
            );
}
}
