package com.esl.academy.api.dashboard;

import com.esl.academy.api.security.AuthenticationService;
import com.esl.academy.api.security.configuration.CustomUserDetails;
import com.esl.academy.api.user.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.esl.academy.api.dashboard.DashboardDto.*;

@Tag(name = "Dashboard")
@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class DashboardController {

    private final SuperAdminDashboardService superAdminDashboardService;
    private final SupervisorDashboardService supervisorDashboardService;
    private final InternDashboardService internDashboardService;
    private final AuthenticationService authService;

    @Operation(summary = "Get intern dashboard")
    @GetMapping("intern")
    @PreAuthorize("hasRole('INTERN')")
    public InternDashboardDto getInternDashboard() {
       final var user = authService.getAuthenticatedUser();
        return internDashboardService.getInternDashboard(user);
    }

    @Operation(summary = "Get super admin dashboard")
    @GetMapping("super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public SuperAdminDashboardDto getSuperAdminDashboard() {
        return superAdminDashboardService.getSuperAdminDashboard();
    }

    @Operation(summary = "Get supervisor dashboard")
    @GetMapping("supervisor")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public SupervisorDashboardDto getSupervisorDashboard(Authentication authentication) {
        final var user = authService.getAuthenticatedUser();
        return supervisorDashboardService.getSupervisorDashboard(user);
    }
}
