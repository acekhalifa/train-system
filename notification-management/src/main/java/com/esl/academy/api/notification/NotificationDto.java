package com.esl.academy.api.notification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
    UUID notificationId,
    UUID userId,
    String title,
    String message,
    Boolean isRead,
    Boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime readAt,
    String createdBy,
    String modifiedBy
) {
    public record AddNotificationDto(
        @NotNull UUID userId,
        @Size(min = 1, max = 256) String title,
        String message
    ) {
    }
}
