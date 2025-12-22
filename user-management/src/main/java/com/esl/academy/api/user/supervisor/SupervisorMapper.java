package com.esl.academy.api.user.supervisor;

import com.esl.academy.api.user.Supervisor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupervisorMapper {

   SupervisorMapper INSTANCE = Mappers.getMapper(SupervisorMapper.class);

   SupervisorDto map(Supervisor supervisor);
}
