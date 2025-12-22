package com.esl.academy.api.submission;

import com.esl.academy.api.core.exceptions.AuthorizationException;
import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.Document;
import com.esl.academy.api.DocumentDto;
import com.esl.academy.api.DocumentService;
import com.esl.academy.api.assessment.Assessment;
import com.esl.academy.api.assessment.AssessmentService;
import com.esl.academy.api.assessment.PublishedStatus;
import com.esl.academy.api.core.exceptions.ConflictException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.security.AuthenticationService;
import com.esl.academy.api.submission.event.SubmissionCreatedEvent;
import com.esl.academy.api.submission.link.Link;
import com.esl.academy.api.submission.link.LinkService;
import com.esl.academy.api.submission.link.LinkType;
import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.user.InternStatus;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.intern.InternService;
import com.esl.academy.api.user.user.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.esl.academy.api.submission.SubmissionMapper.INSTANCE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final DocumentService documentService;
    private final LinkService linkService;
    private final UserService userService;
    private final InternService internService;
    private final AssessmentService assessmentService;
    private final TrackService trackService;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher appEventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SubmissionDto.SubmissionResponseDto submitAssessment(SubmissionDto.SubmissionRequestDto dto,
                                                                List<MultipartFile> files,
                                                                UUID assessmentId) throws IOException {
        // Validate intern status
        var intern = internService.getInternById(authenticationService.getAuthenticatedUser().getUserId());

        if (intern.internStatus() != InternStatus.ACTIVE) {
            throw new BadRequestException("Intern is not active");
        }

        // Prevent duplicate submission
        boolean alreadySubmitted =
            submissionRepository.existsByIntern_UserIdAndAssessment_AssessmentId(
                intern.userId(),
                assessmentId
            );

        if (alreadySubmitted) {
            throw new ConflictException("Assessment already submitted");
        }

        // Assessment validations
        var optionalAssessment = assessmentService.getAssessmentById(assessmentId);


        if (optionalAssessment.isDeleted()) {
            throw new BadRequestException("Assessment has been deleted");
        }

        if (optionalAssessment.publishedStatus() != PublishedStatus.PUBLISHED) {
            throw new BadRequestException("Assessment is not published");
        }

        if (optionalAssessment.deadline().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("Assessment deadline has passed");
        }

        // Validate User has the same track as assessmentDto
        UUID internTrackId = intern.track().trackId();
        UUID assessmentTrackId = optionalAssessment.trackId();



        if (!internTrackId.equals(assessmentTrackId)) {
            throw new BadRequestException("Intern does not belong to this assessment track");
        }

        // Validate file

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("At least one submission file is required");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BadRequestException("One of the submitted files is empty.");
            }
        }

        entityManager.flush();

        // Save submission
        Assessment assessment = assessmentService.getById(assessmentId).get();
        Submission submission = new Submission();
        submission.setIntern(authenticationService.getAuthenticatedUser().getIntern());
        submission.setAssessment(assessment);
        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submission.setGradingStatus(GradingStatus.NOT_GRADED);
        submission.setSubmissionNote(dto.submissionNote());
        submission.setScore(0);
        submission.setDeleted(false);

        Submission savedSubmission = submissionRepository.save(submission);

        // Attach documents to submission
        List<DocumentDto> savedDocs = documentService.addDocuments(files);

        List<UUID> documentIds = savedDocs.stream()
            .map(DocumentDto::documentId)
            .toList();

        List<Document> documents =
            documentService.findAllById(documentIds);

        savedSubmission.getDocuments().addAll(documents);

        // Save links provided in submission

        for(String link : dto.submissionLinks()) {
            String linkTitle = "ASSESSMENT SUBMISSION";
            Link submissionLink = new Link();
            submissionLink.setUrl(link);
            submissionLink.setTitle(linkTitle);
            submissionLink.setObjectId(savedSubmission.getSubmissionId());
            submissionLink.setLinkType(LinkType.SUBMISSION);
            linkService.save(submissionLink);
        }

        SubmissionDto submissionDto = INSTANCE
            .map(savedSubmission,
                linkService.getUrls(savedSubmission.getSubmissionId(),LinkType.SUBMISSION),
                trackService
            );
        var event = new SubmissionCreatedEvent(submissionDto.fullName(), submissionDto.trackName());
        appEventPublisher.publishEvent(event);

        return new SubmissionDto.SubmissionResponseDto(
            submissionDto.assessmentDescription(),
            submissionDto.submissionLinks(),
            submissionDto.taskTitle(),
            submissionDto.documentNames(),
            submissionDto.score(),
            submissionDto.feedback()
        );
    }

    public void gradeAssessment(UUID submissionId, SubmissionDto.GradingDto gradingDto) {
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        var supervisor = userService.getUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new NotFoundException("Submission not found"));

        if (submission.getGradingStatus().equals(GradingStatus.GRADED)) {
            throw new BadRequestException("Assessment has already been graded");
        }
        var submissionTrack = submission.getAssessment().getLearningResource().getTrack();
        UUID supervisorTrack = supervisor.getTrackId();
        boolean isAssignedSupervisor =
            supervisorTrack.equals(submissionTrack.getTrackId());

        if (!isAssignedSupervisor) {
            throw new AuthorizationException("Supervisor not assigned to this track");
        }


        submission.setScore(gradingDto.score());
        submission.setFeedback(gradingDto.feedback());
        submission.setGradingStatus(GradingStatus.GRADED);
        submissionRepository.save(submission);

    }

    public List<SubmissionDto.SubmittedAssessmentDto> getAllSubmissionsByIntern() {
        User user = authenticationService.getAuthenticatedUser();
        List<SubmissionDto> submissionDtos = INSTANCE.map(submissionRepository.findByIntern_UserId(user.getUserId()),trackService);
        return submissionDtos.stream()
            .map(submissionDto -> new SubmissionDto
                .SubmittedAssessmentDto(submissionDto.assessmentId(),submissionDto.taskTitle())
            )
            .collect(Collectors.toList());
    }

    public SubmissionDto.GradingDto getFeedbackForSubmission(UUID assessmentId) {
        User user = authenticationService.getAuthenticatedUser();
        Submission submission = submissionRepository
            .findByIntern_UserIdAndAssessment_AssessmentId(user.getUserId(), assessmentId)
            .orElseThrow(() -> new NotFoundException("Submission not found"));

        if (submission.getGradingStatus() != GradingStatus.GRADED) {
            throw new BadRequestException("Submission not yet graded");
        }

        return new SubmissionDto.GradingDto(submission.getScore(), submission.getFeedback());
    }

    public List<SubmissionDto.InternSubmissionResponseDto> getAllSubmissionsForTrack() {

        var supervisor = authenticationService.getAuthenticatedUser();
        List<Assessment> assessments =
            assessmentService.getAssessmentsByTrack(supervisor.getTrackId());

        List<UUID> assessmentIds = assessments.stream()
            .map(Assessment::getAssessmentId)
            .toList();

        List<Submission> submissions = submissionRepository
            .findByAssessment_AssessmentIdIn(assessmentIds);

        var submissionDtos = INSTANCE.map(submissions,trackService);

        return submissionDtos.stream()
            .map(dto -> new SubmissionDto.InternSubmissionResponseDto(
                dto.fullName(),
                dto.trackName(),
                dto.taskTitle(),
                dto.submissionStatus(),
                dto.submittedAt(),
                dto.gradingStatus()
            ))
            .toList();
    }

    public SubmissionDto.SubmissionPreviewResponseDto getSubmissionPreview(UUID submissionId) {
        var user = authenticationService.getAuthenticatedUser();
        Submission submission = submissionRepository
            .findById(submissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        SubmissionDto dto = INSTANCE.map(
            submission,
            linkService.getUrls(submission.getSubmissionId(),LinkType.SUBMISSION),
            trackService
        );
        return new SubmissionDto.SubmissionPreviewResponseDto(
            dto.fullName(),
            dto.trackName(),
            dto.taskTitle(),
            dto.submissionNote(),
            dto.submissionLinks(),
            dto.documents()
        );
    }

    public long countSubmissionsByIntern(UUID internId){
        return submissionRepository.countByInternIdAndSubmissionStatus(internId, SubmissionStatus.SUBMITTED.name());
    }
    public long countGradedSubmissionsByIntern(UUID internId){
        return submissionRepository.countByInternIdAndGradingStatus(internId, GradingStatus.GRADED.name());
    }

    public long countNotGradedSubmissionsByIntern(UUID internId){
        return submissionRepository.countByInternIdAndGradingStatus(internId, GradingStatus.NOT_GRADED.name());
    }

    public long countMissedSubmissionsByIntern(UUID internId){
        return submissionRepository.countMissedAssessmentsByInternId(internId);
    }

    public double getAverageSubmissionsScoreForTrack(UUID trackId){
        var score = submissionRepository.getAverageGradedSubmissionsScoreForTrack(trackId);
        return score != null ? score : 0.0;
    }

    public long getPendingSubmissionsCountForTrack(UUID trackId) {
        return submissionRepository.countSubmittedNotGradedByTrack(trackId);
    }
}

