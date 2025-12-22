package com.esl.academy.api.dashboard;

import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.user.intern.InternService;
import com.esl.academy.api.user.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminDashboardService {
    private final InternService internService;
    private final UserService userService;
    private final TrackService trackService;

    public DashboardDto.SuperAdminDashboardDto getSuperAdminDashboard() {
        final var totalInterns = internService.getAllInterns(Pageable.unpaged()).getTotalElements();
        final var totalSupervisors = userService.getAllSupervisors(Pageable.unpaged()).getTotalElements();
        final var totalSuperAdmins = userService.getAllSuperAdmins(Pageable.unpaged()).getTotalElements();
        final var totalTracks = trackService.getAllTracks(Pageable.unpaged()).getTotalElements();

        return new DashboardDto.SuperAdminDashboardDto(
            totalInterns,
            totalSupervisors,
            totalSuperAdmins,
            totalTracks
        );
    }
}
