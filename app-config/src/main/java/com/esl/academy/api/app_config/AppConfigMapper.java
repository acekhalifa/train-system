package com.esl.academy.api.app_config;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface AppConfigMapper {

    AppConfigMapper INSTANCE = Mappers.getMapper(AppConfigMapper.class);

    AppConfigDto map(AppConfig config);

    List<AppConfigDto> map(List<AppConfig> configs);
}
