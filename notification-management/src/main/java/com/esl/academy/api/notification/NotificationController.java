package com.esl.academy.api.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static com.esl.academy.api.notification.NotificationDto.AddNotificationDto;

@Tag(name = "Notification")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class NotificationController {
    private final NotificationService service;

    @Operation(summary = "Create a notification")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public NotificationDto addNotification(
        @RequestBody @Valid AddNotificationDto dto) {
        return service.addNotification(dto);
    }

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/{notificationId}/read")
    public NotificationDto markAsRead(@PathVariable UUID notificationId) {
        return service.markAsRead(notificationId);
    }

    @Operation(summary = "Mark all notifications as read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{userId}/read-all")
    public void markAllAsRead(@PathVariable UUID userId) {
        service.markAllAsRead(userId);
    }

    @Operation(summary = "Mark a notification as unread")
    @PutMapping("/{notificationId}/unread")
    public NotificationDto markAsUnread(@PathVariable UUID notificationId) {
        return service.markAsUnread(notificationId);
    }

    @Operation(summary = "Fetch latest notification")
    @GetMapping("/{userId}/latest")
    public Optional<NotificationDto> getLatestNotification(@PathVariable UUID userId) {
        return service.getLatestNotification(userId);
    }

    @Operation(summary = "Fetch all notifications")
    @GetMapping("/{userId}")
    public List<NotificationDto> getAllNotifications(@PathVariable UUID userId) {
        return service.getAllNotifications(userId);
    }

    @Operation(summary = "Soft delete a notification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{notificationId}")
    public void softDelete(@PathVariable UUID notificationId) {
        service.softDelete(notificationId);
    }
}
