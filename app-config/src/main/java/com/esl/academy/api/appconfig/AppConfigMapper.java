package com.esl.academy.api.appconfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface AppConfigMapper {

    AppConfigMapper INSTANCE = Mappers.getMapper(AppConfigMapper.class);

    @Mapping(source = "id", target = "appConfigId")
    AppConfigDTO toDto(AppConfig config);

    List<AppConfigDTO> toDtoList(List<AppConfig> configs);
}
