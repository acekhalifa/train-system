package com.esl.academy.api.options.option;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.options.option_type.OptionType;
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
        final var optionType = optionTypeRepository
            .findById(request.optionTypeId()).
            orElseThrow(() ->
                new NotFoundException("OptionType not found"));
        var option = Option.builder()
            .name(request.name())
            .optionType(optionType)
            .description(request.description())
            .build();
        return INSTANCE.toOptionDto(optionRepository.save(option));
    }

    public List<OptionDto> getOptionsByType(UUID optionTypeId) {
        return INSTANCE
            .toOptionDtoList(optionRepository.findByOptionType_OptionTypeId(optionTypeId));
    }

    public OptionDto getOptionById(UUID optionId) {
        final var option = optionRepository.findById(optionId)
            .orElseThrow(() -> new NotFoundException("Option not found"));
        return INSTANCE.toOptionDto(option);
    }

    public List<Option> findByOptionType(OptionType optionType) {
        return optionRepository.findByOptionType(optionType);
    }

    public OptionDto updateOption(UUID optionId, AddUpdateOptionDto request) {
        var existingOption = optionRepository.findById(optionId)
            .orElseThrow(() -> new NotFoundException("Option not found"));
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
                    throw new NotFoundException("Option not found");
                }
            );
    }
}
