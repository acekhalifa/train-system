package com.esl.academy.api.integration.tests.submission;

import com.esl.academy.api.Document;
import com.esl.academy.api.DocumentRepository;
import com.esl.academy.api.assessment.Assessment;
import com.esl.academy.api.assessment.AssessmentRepository;
import com.esl.academy.api.assessment.PublishedStatus;
import com.esl.academy.api.learning_resource.LearningResource;
import com.esl.academy.api.submission.GradingStatus;
import com.esl.academy.api.submission.Submission;
import com.esl.academy.api.submission.SubmissionRepository;
import com.esl.academy.api.submission.SubmissionStatus;
import com.esl.academy.api.submission.link.Link;
import com.esl.academy.api.submission.link.LinkRepository;
import com.esl.academy.api.submission.link.LinkType;
import com.esl.academy.api.track.Track;
import com.esl.academy.api.track.TrackRepository;
import com.esl.academy.api.user.Intern;
import com.esl.academy.api.user.InternStatus;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserType;
import com.esl.academy.api.user.intern.InternRepository;
import com.esl.academy.api.user.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class TestData {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private InternRepository internRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private LinkRepository linkRepository;

    public Track createTrack(String name, UUID trackId) {
        Track track = Track.builder()
            .name(name)
            .trackId(trackId)
            .build();
        return trackRepository.save(track);
    }

    public User createUser(UUID id,
                           String email,
                           String firstName,
                           String lastName,
                           String password) {
        User user = User.builder()
            .userId(id)
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .password(password)
            .build();
        return userRepository.save(user);
    }

    public Intern createIntern(UUID id,
                               String email,
                               String firstName,
                               String lastName,
                               String password,
                               Track track,
                               InternStatus status) {
        User user = createUser(
            id,
            email,
            firstName,
            lastName,
            password
        );
        Intern intern = Intern.builder()
            .user(user)
            .track(track)
            .internStatus(status)
            .build();
        return internRepository.save(intern);
    }

    public User createSupervisor(UUID id,
                                 String email,
                                 String firstName,
                                 String lastName,
                                 String password,
                                 UUID trackId) {
        User supervisor = createUser(
            id,
            email,
            firstName,
            lastName,
            password
        );

        supervisor.setUserType(UserType.SUPERVISOR);
        supervisor.setTrackId(trackId);

        return supervisor;
    }

    public LearningResource createLearningResource(UUID id,
                                                   Track track,
                                                   String resourceTitle) {
        return LearningResource.builder()
            .learningResourceId(id)
            .resourceTitle(resourceTitle)
            .track(track)
            .build();
    }

    public Assessment createAssessment(LearningResource learningResource,
                                       PublishedStatus publishedStatus,
                                       OffsetDateTime deadline,
                                       boolean deleted) {
        Assessment assessment = Assessment.builder()
            .learningResource(learningResource)
            .publishedStatus(publishedStatus)
            .deadline(deadline)
            .isDeleted(deleted)
            .build();
        return assessmentRepository.save(assessment);
    }

    public Submission createSubmission(Intern intern,
                                       Assessment assessment,
                                       SubmissionStatus submissionStatus,
                                       GradingStatus gradingStatus,
                                       String note, int score,
                                       boolean deleted) {
        Submission submission = Submission.builder()
            .intern(intern)
            .assessment(assessment)
            .submissionStatus(submissionStatus)
            .gradingStatus(gradingStatus)
            .submissionNote(note)
            .score(score)
            .isDeleted(deleted)
            .build();
        return submissionRepository.save(submission);
    }

    public Document createDocument(String name, UUID id) {
        Document document = Document.builder()
            .name(name)
            .documentId(id)
            .build();
        return documentRepository.save(document);
    }

    public Link createLink(String url,
                           String title,
                           UUID objectId,
                           LinkType type) {
        Link link = Link.builder()
            .url(url)
            .title(title)
            .objectId(objectId)
            .linkType(type).build();
        return linkRepository.save(link);
    }
}
