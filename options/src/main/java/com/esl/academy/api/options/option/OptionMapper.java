package com.esl.academy.api.options.option;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface OptionMapper {

    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);

    @Mapping(source = "optionType.optionTypeId", target = "optionTypeId")
    OptionDto toOptionDto(Option option);

    List<OptionDto> toOptionDtoList(List<Option> options);
}
