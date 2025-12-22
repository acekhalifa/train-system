package com.esl.academy.api.notification.listener;

import com.esl.academy.api.notification.NotificationDto;
import com.esl.academy.api.notification.NotificationService;
import com.esl.academy.api.submission.event.SubmissionCreatedEvent;
import com.esl.academy.api.track.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SubmissionNotificationListener {
    private final TrackService trackService;
    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void handleSubmissionCreated(SubmissionCreatedEvent event) {
        final var trackId = trackService.getTrackByName(event.trackName()).trackId();
        final var supervisors = trackService.getAllSupervisorsForTrack(trackId);
        final var message = event.internName() + " made a submission";
        supervisors.forEach(supervisor -> {
            notificationService.addNotification(
                new NotificationDto.AddNotificationDto(supervisor.getUserId(), message, message));
        });
    }
}
