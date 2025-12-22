package com.esl.academy.api.submission;

import com.esl.academy.api.DocumentDto;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SubmissionDto(String fullName,
                            SubmissionStatus submissionStatus,
                            GradingStatus gradingStatus,
                            String trackName,
                            UUID assessmentId,
                            String assessmentDescription,
                            String submissionNote,
                            List<String> submissionLinks,
                            String taskTitle,
                            List<String> documentNames,
                            int score,
                            String feedback,
                            OffsetDateTime submittedAt,
                            List<DocumentDto.DocumentResponseDto> documents) {

    public record SubmissionResponseDto(String assessmentDescription,
                                        List<String> submissionLinks,
                                        String resourceTitle,
                                        List<String> documentNames,
                                        int score,
                                        String feedback){}

    public record SubmissionRequestDto(String submissionNote,
                                       @NotNull List<String> submissionLinks){}

    public record SubmittedAssessmentDto(UUID assessmentId,
                                         String resourceTitle){}

    public record InternSubmissionResponseDto(String fullName,
                                              String trackName,
                                              String taskTitle,
                                              SubmissionStatus submissionStatus,
                                              OffsetDateTime submittedAt,
                                              GradingStatus gradingStatus){}

    public record SubmissionPreviewResponseDto(String fullName,
                                               String trackName,
                                               String taskTitle,
                                               String submissionNote,
                                               List<String> submissionLinks,
                                               List<DocumentDto.DocumentResponseDto> documents){}
    public record GradingDto(@NotNull int score,
                             String feedback){}

}
