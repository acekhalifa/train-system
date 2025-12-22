package com.esl.academy.api.dashboard;

import com.esl.academy.api.notification.NotificationDto;

import java.util.List;

public record DashboardDto (){
    public record InternDashboardDto(
        long submittedAssessments,
        long missedAssessments,
        long gradedAssessments,
        long pendingAssessments
    ) {}
    public record SuperAdminDashboardDto(
        long totalInterns,
        long totalSupervisors,
        long totalSuperAdmins,
        long totalTracks
    ) {}
    public record SupervisorDashboardDto(
        long totalInterns,
        long totalResources,
        long pendingTasks,
        double averageScore,
        List<NotificationDto> notifications
    ) {}
}
