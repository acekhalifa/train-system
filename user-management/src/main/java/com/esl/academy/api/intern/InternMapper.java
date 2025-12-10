package com.esl.academy.api.intern;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InternMapper {

    InternMapper INSTANCE = Mappers.getMapper(InternMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    InternDto toDto(Intern intern);
}
