package com.esl.academy.api.notification.listener;

import com.esl.academy.api.notification.NotificationDto;
import com.esl.academy.api.notification.NotificationService;
import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.event.InternJoinedTrackEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternEventNotificationListener {

    private final TrackService trackService;
    private final NotificationService notificationService;

    @EventListener
    public void handleInternJoined(InternJoinedTrackEvent event) {
        final var supervisors = trackService.getAllSupervisorsForTrack(event.trackId());
        final var message =  "new intern joined your track";
        supervisors.forEach(supervisor -> {
            notificationService.addNotification(
                new NotificationDto.AddNotificationDto(supervisor.getUserId(), message, message));
        });
    }
}
