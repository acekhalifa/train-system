package com.esl.academy.api.notification;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);
    NotificationDto toDto(Notification notification);
    Notification toEntity(NotificationDto notificationDto);
}
