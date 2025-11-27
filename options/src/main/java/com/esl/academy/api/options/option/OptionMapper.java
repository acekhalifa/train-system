package com.esl.academy.api.options.option;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OptionMapper {

    OptionDto toOptionDto(Option option);
    List<OptionDto> toOptionDtoList(List<Option> options);
}
