package com.esl.academy.api.submission;

import com.esl.academy.api.Document;
import com.esl.academy.api.DocumentMapper;
import com.esl.academy.api.track.TrackService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(uses = DocumentMapper.class)
public interface SubmissionMapper {

    SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

    @Mapping(target = "assessmentDescription" , expression = "java(submission.getAssessment().getDescription())")
    @Mapping(target = "assessmentId" , expression = "java(submission.getAssessment().getAssessmentId())")
    @Mapping(target = "submissionLinks", expression = "java(links)")
    @Mapping(target = "taskTitle", expression = "java(submission.getAssessment().getLearningResource().getResourceTitle())")
    @Mapping(target = "documentNames", expression = "java(submission.getDocuments().stream().map(d -> d.getName()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "fullName", expression = "java(submission.getIntern().getUser().getName())")
    @Mapping(target = "trackName", expression = "java(trackService.getTrackNameById(submission.getAssessment().getLearningResource().getTrack().getTrackId()))")
    @Mapping(target = "submittedAt" , source = "createdAt")
    SubmissionDto map(Submission submission, @Context List<String> links, @Context TrackService trackService);

    default List<SubmissionDto> map(List<Submission> submissions, TrackService trackService) {
        return submissions.stream()
            .map(sub -> map(sub, sub.getDocuments().stream().map(Document::getName).collect(Collectors.toList()),trackService))
            .collect(Collectors.toList());
    }
}
