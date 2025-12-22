package com.esl.academy.api.track;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrackMapper {

    TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

    TrackDto map(Track track);
}
