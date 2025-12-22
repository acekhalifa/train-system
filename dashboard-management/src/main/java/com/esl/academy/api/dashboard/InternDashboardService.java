package com.esl.academy.api.dashboard;

import com.esl.academy.api.submission.SubmissionService;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternDashboardService {

    private final SubmissionService submissionService;

    public DashboardDto.InternDashboardDto getInternDashboard(User intern) {
        if (intern.getUserType() != UserType.INTERN) {
            throw new SecurityException("User is not an intern");
        }
        final var internId = intern.getUserId();
        final var submittedAssessments = submissionService.countSubmissionsByIntern(internId);
        final var missedAssessments = submissionService.countMissedSubmissionsByIntern(internId);
        final var gradedAssessments = submissionService.countGradedSubmissionsByIntern(internId);
        final var pendingAssessments = submissionService.countNotGradedSubmissionsByIntern(internId);

        return new DashboardDto.InternDashboardDto(
            submittedAssessments,
            missedAssessments,
            gradedAssessments,
            pendingAssessments
        );
    }
}
