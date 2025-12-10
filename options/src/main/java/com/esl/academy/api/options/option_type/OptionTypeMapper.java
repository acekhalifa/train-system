package com.esl.academy.api.options.option_type;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface OptionTypeMapper {

    OptionTypeMapper INSTANCE = Mappers.getMapper(OptionTypeMapper.class);

    OptionTypeDto map(OptionType optionType);

    List<OptionTypeDto> map(List<OptionType> optionTypes);
}
