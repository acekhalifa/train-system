package com.esl.academy.api.learning_resource;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LearningResourceMapper {

    LearningResourceMapper INSTANCE = Mappers.getMapper(LearningResourceMapper.class);

    LearningResourceDto toDto(LearningResource learningResource);
}
