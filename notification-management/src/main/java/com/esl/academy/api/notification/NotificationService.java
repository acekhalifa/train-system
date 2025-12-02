package com.esl.academy.api.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.esl.academy.api.core.exceptions.NotFoundException;
import static com.esl.academy.api.notification.NotificationDto.AddNotificationDto;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    public final NotificationRepository repository;

    public NotificationDto addNotification(AddNotificationDto dto) {
        Notification notification = Notification.builder()
            .userId(dto.userId())
            .title(dto.title())
            .message(dto.message())
            .isRead(false)
            .active(true)
            .createdAt(OffsetDateTime.now())
            .createdBy("{\"userId\": \"" + dto.userId() + "\"}")
            .build();

        Notification saved = repository.save(notification);
        return NotificationMapper.INSTANCE.toDto(saved);
    }

    public NotificationDto markAsRead(UUID notificationId) {
        Notification notification = repository.findById(notificationId)
            .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setIsRead(true);
        notification.setReadAt(OffsetDateTime.now());

        Notification updated = repository.save(notification);
        return NotificationMapper.INSTANCE.toDto(updated);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(OffsetDateTime.now());
        });

        repository.saveAll(notifications);
    }

    public NotificationDto markAsUnread(UUID notificationId) {
        Notification notification = repository.findById(notificationId)
            .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setIsRead(false);
        notification.setReadAt(null);

        return NotificationMapper.INSTANCE.toDto(repository.save(notification));
    }

    public Optional<NotificationDto> getLatestNotification(UUID userId) {
        return repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId)
            .stream()
            .findFirst()
            .map(NotificationMapper.INSTANCE::toDto);
    }

    public List<NotificationDto> getAllNotifications(UUID userId) {
        return repository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId)
            .stream()
            .map(NotificationMapper.INSTANCE::toDto)
            .toList();
    }

    public void softDelete(UUID notificationId) {
        Notification notification = repository.findById(notificationId)
            .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setActive(false);
        repository.save(notification);
    }
}
