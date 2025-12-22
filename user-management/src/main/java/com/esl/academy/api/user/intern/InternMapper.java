package com.esl.academy.api.user.intern;

import com.esl.academy.api.user.Intern;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InternMapper {

    InternMapper INSTANCE = Mappers.getMapper(InternMapper.class);

    InternDto map(Intern intern);
}
