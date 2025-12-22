package com.esl.academy.api.dashboard;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.learning_resource.LearningResourceService;
import com.esl.academy.api.notification.NotificationService;
import com.esl.academy.api.submission.SubmissionService;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserType;
import com.esl.academy.api.user.intern.InternService;
import com.esl.academy.api.user.supervisor.SupervisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupervisorDashboardService{
    private final SupervisorService supervisorService;
    private final InternService internService;
    private final LearningResourceService learningResourceService;
    private final SubmissionService submissionService;
    private final NotificationService notificationService;

    public DashboardDto.SupervisorDashboardDto getSupervisorDashboard(User user) {
        if (user.getUserType() != UserType.SUPERVISOR) {
            throw new SecurityException("User is not a supervisor");
        }
        final var supervisor = supervisorService.getSupervisorById(user.getUserId());
        final var trackId = supervisor.tracks().stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Track not found"))
            .trackId();

        final var totalInterns = internService.getAllInterns(Pageable.unpaged()).getTotalElements();
        final var totalResources = learningResourceService.getAllLearningResources(trackId,
            Pageable.unpaged()).getTotalElements();
        final var pendingTasks = submissionService.getPendingSubmissionsCountForTrack(trackId);
        final var averageScore = submissionService.getAverageSubmissionsScoreForTrack(trackId);
        final var notifications = notificationService.getAllNotifications(user.getUserId());
        return new DashboardDto.SupervisorDashboardDto(
            totalInterns,
            totalResources,
            pendingTasks,
            averageScore,
            notifications
        );
    }
}
