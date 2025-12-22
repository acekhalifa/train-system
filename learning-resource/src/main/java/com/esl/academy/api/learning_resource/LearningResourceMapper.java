package com.esl.academy.api.learning_resource;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LearningResourceMapper {

    LearningResourceMapper INSTANCE = Mappers.getMapper(LearningResourceMapper.class);

    @Mapping(source = "track.name", target = "track")
    @Mapping(source = "month.name", target = "month")
    @Mapping(source = "week.name", target = "week")
    LearningResourceDto map(LearningResource learningResource);


    default LearningResource map(LearningResourceDto dto) {
        LearningResource lr = new LearningResource();
        lr.setResourceTitle(dto.resourceTitle());
        lr.setDescription(dto.description());
        lr.setDeleted(dto.isDeleted());
        return lr;
    }
}
