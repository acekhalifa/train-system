package com.esl.academy.api.options.optiontype;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper
public interface OptionTypeMapper {

    OptionTypeDto toOptionTypeDto(OptionType optionType);
    List<OptionTypeDto> toOptionTypeDtoList(List<OptionType> optionTypes);
}
