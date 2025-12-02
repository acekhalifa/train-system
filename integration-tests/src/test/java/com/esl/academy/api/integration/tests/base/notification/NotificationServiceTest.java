package com.esl.academy.api.integration.tests.base.notification;

import com.esl.academy.api.notification.NotificationDto;
import com.esl.academy.api.notification.Notification;
import com.esl.academy.api.notification.NotificationRepository;
import com.esl.academy.api.notification.NotificationService;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.esl.academy.api.notification.NotificationDto.*;

public class NotificationServiceTest extends BaseIntegrationTest {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private final UUID TEST_USER_ID = UUID.randomUUID();
    private final UUID OTHER_USER_ID = UUID.randomUUID();
    private final UUID CREATED_BY_ID = UUID.randomUUID();
    private final String TEST_USER_CREATED_BY = "{\"userId\": \"%s\"}".formatted(TEST_USER_ID);

    @BeforeEach
    void setup() {
        // Clear the repository before each test to ensure test isolation
        notificationRepository.deleteAll();
    }

    @Test
    void addNotification_ShouldCreateAndReturnNotificationDto() {
        // Arrange
        AddNotificationDto newNotification = new AddNotificationDto(
            TEST_USER_ID,
            "Welcome!",
            "Thank you for joining our platform."
        );

        NotificationDto resultDto = notificationService.addNotification(newNotification);

        Assertions.assertThat(resultDto).isNotNull();
        Assertions.assertThat(resultDto.userId()).isEqualTo(TEST_USER_ID);
        Assertions.assertThat(resultDto.title()).isEqualTo("Welcome!");
        Assertions.assertThat(resultDto.isRead()).isFalse();
        Assertions.assertThat(resultDto.active()).isTrue();
        Assertions.assertThat(notificationRepository.count()).isEqualTo(1);

        Notification savedEntity = notificationRepository.findById(resultDto.notificationId()).get();
        Assertions.assertThat(savedEntity.getCreatedBy()).isEqualTo(TEST_USER_CREATED_BY);
    }

    @Test
    void markAsRead_ShouldUpdateIsReadAndReadAt() {
        Notification initialNotification = Notification.builder()
            .userId(TEST_USER_ID)
            .title("Test")
            .message("Msg")
            .build();
        initialNotification.setCreatedAt(OffsetDateTime.now());
        initialNotification.setCreatedBy(TEST_USER_CREATED_BY);
        initialNotification = notificationRepository.save(initialNotification);
        UUID notificationId = initialNotification.getNotificationId();

        NotificationDto updatedDto = notificationService.markAsRead(notificationId);

        Assertions.assertThat(updatedDto.isRead()).isTrue();
        Assertions.assertThat(updatedDto.readAt()).isNotNull();

        Notification updatedEntity = notificationRepository.findById(notificationId).get();
        Assertions.assertThat(updatedEntity.getIsRead()).isTrue();
        Assertions.assertThat(updatedEntity.getReadAt()).isNotNull();
    }

    @Test
    void markAsRead_ShouldThrowNotFoundException_WhenNotificationDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(com.esl.academy.api.core.exceptions.NotFoundException.class,
            () -> notificationService.markAsRead(nonExistentId));
    }

    @Test
    void markAllAsRead_ShouldUpdateAllActiveUserNotifications() {
        Notification n1 = Notification.builder().userId(TEST_USER_ID).title("N1").build();
        n1.setCreatedAt(OffsetDateTime.now());
        n1.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n1);

        Notification n2 = Notification.builder().userId(TEST_USER_ID).title("N2").build();
        n2.setCreatedAt(OffsetDateTime.now());
        n2.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n2);

        Notification n3 = Notification.builder().userId(OTHER_USER_ID).title("N3").build();
        n3.setCreatedAt(OffsetDateTime.now());
        n3.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n3);

        Notification n4 = Notification.builder().userId(TEST_USER_ID).title("N4").active(false).build(); // Inactive
        n4.setCreatedAt(OffsetDateTime.now());
        n4.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n4);

        notificationService.markAllAsRead(TEST_USER_ID);

        List<Notification> testUserNotifications = notificationRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(TEST_USER_ID);
        assertThat(testUserNotifications).hasSize(2);
        assertThat(testUserNotifications).allMatch(Notification::getIsRead);
        assertThat(testUserNotifications).allMatch(n -> n.getReadAt() != null);

        Notification otherUserNotif = notificationRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(OTHER_USER_ID).get(0);
        Assertions.assertThat(otherUserNotif.getIsRead()).isFalse();
        Assertions.assertThat(otherUserNotif.getReadAt()).isNull();
    }

    @Test
    void getAllNotifications_ShouldReturnOnlyActiveNotificationsForUser_SortedByCreatedAt() {
        Notification n1 = Notification.builder().userId(TEST_USER_ID).title("Oldest").build();
        n1.setCreatedAt(OffsetDateTime.now().minusDays(2));
        n1.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n1);

        Notification n2 = Notification.builder().userId(TEST_USER_ID).title("Newest").build();
        n2.setCreatedAt(OffsetDateTime.now().minusDays(1));
        n2.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n2);

        Notification n3 = Notification.builder().userId(TEST_USER_ID).title("Inactive").active(false).build();
        n3.setCreatedAt(OffsetDateTime.now());
        n3.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n3);

        Notification n4 = Notification.builder().userId(OTHER_USER_ID).title("Other User").build();
        n4.setCreatedAt(OffsetDateTime.now());
        n4.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(n4);

        List<NotificationDto> resultList = notificationService.getAllNotifications(TEST_USER_ID);

        assertThat(resultList).hasSize(2);
        // Assert sorting (OrderByCreatedAtDesc)
        Assertions.assertThat(resultList.get(0).title()).isEqualTo("Newest");
        Assertions.assertThat(resultList.get(1).title()).isEqualTo("Oldest");
    }

    @Test
    void softDelete_ShouldSetNotificationAsInactive() {
        Notification notification = Notification.builder()
            .userId(TEST_USER_ID)
            .title("To Delete")
            .build();
        notification.setCreatedAt(OffsetDateTime.now());
        notification.setCreatedBy(TEST_USER_CREATED_BY);
        notification = notificationRepository.save(notification);
        UUID notificationId = notification.getNotificationId();

        notificationService.softDelete(notificationId);

        Notification deletedEntity = notificationRepository.findById(notificationId).get();
        Assertions.assertThat(deletedEntity.getActive()).isFalse();

        // Verify it's excluded from active fetches
        List<NotificationDto> activeNotifications = notificationService.getAllNotifications(TEST_USER_ID);
        assertThat(activeNotifications).isEmpty();
    }

    @Test
    void getLatestNotification_ShouldReturnNewestActiveNotification() {
        Notification old = Notification.builder().userId(TEST_USER_ID).title("Old").build();
        old.setCreatedAt(OffsetDateTime.now().minusDays(5));
        old.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(old);

        Notification latest = Notification.builder().userId(TEST_USER_ID).title("Latest").build();
        latest.setCreatedAt(OffsetDateTime.now().minusDays(1));
        latest.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(latest);

        Notification inactive = Notification.builder().userId(TEST_USER_ID).title("Inactive").active(false).build();
        inactive.setCreatedAt(OffsetDateTime.now());
        inactive.setCreatedBy(TEST_USER_CREATED_BY);
        notificationRepository.save(inactive);

        Optional<NotificationDto> latestDto = notificationService.getLatestNotification(TEST_USER_ID);

        assertThat(latestDto).isPresent();
        Assertions.assertThat(latestDto.get().notificationId()).isEqualTo(latest.getNotificationId());
        Assertions.assertThat(latestDto.get().title()).isEqualTo("Latest");
    }
}
