package com.esl.academy.api.assessment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssessmentMapper {

    AssessmentMapper INSTANCE = Mappers.getMapper(AssessmentMapper.class);

    @Mapping(target = "trackId", source = "learningResource.track.trackId")
    AssessmentDto map(Assessment assessment);

}
