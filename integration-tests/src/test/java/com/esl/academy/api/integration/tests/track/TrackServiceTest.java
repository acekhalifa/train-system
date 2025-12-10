package com.esl.academy.api.integration.tests.track;

import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.track.TrackDto;
import com.esl.academy.api.track.TrackRepository;
import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.OffsetDateTime;

import static com.esl.academy.api.track.TrackDto.AddTrackDto;
import static com.esl.academy.api.track.TrackDto.UpdateTrackDto;

public class TrackServiceTest extends BaseIntegrationTest {
    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @BeforeEach
    void setup() {
        trackRepository.deleteAll();
    }

    @Test
    void addTrack_withValidDate_shouldAddTrack() {
        AddTrackDto newTrack = new AddTrackDto(
            "Backend",
            "Powering scalable and high-performant apps",
            OffsetDateTime.parse("2025-12-29T00:00:00+01:00"),
            OffsetDateTime.parse("2026-05-04T23:59:59+01:00"),
            5,
            "restful apis, and database"
        );

        TrackDto resultDto = trackService.addTrack(newTrack);

        Assertions.assertThat(resultDto).isNotNull();
        Assertions.assertThat(resultDto.trackId()).isNotNull();
        Assertions.assertThat(resultDto.name()).isEqualTo("Backend");
        Assertions.assertThat(resultDto.duration()).isEqualTo(5);
    }

    @Test
    void addTrack_withInvalidDate_shouldThrowBadRequestException() {
        AddTrackDto newTrack = new AddTrackDto(
            "Frontend",
            "Building fast and user-centric interfaces",
            OffsetDateTime.parse("2025-12-29T00:00:00+01:00"),
            OffsetDateTime.parse("2025-06-04T23:59:59+01:00"),
            6,
            "angular, react"
        );

        Assertions.assertThatThrownBy(() -> trackService.addTrack(newTrack))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("End date must be after start date");
    }

    @Test
    void addTrack_withDurationMismatch_shoudThrowBadRequestException() {
        AddTrackDto newTrack = new AddTrackDto(
            "DevOps",
            "Automating deployments and CI/CD pipelines",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-28T23:59:59+01:00"),
            5,
            "ci/cd, docker, kubernetes"
        );

        Assertions.assertThatThrownBy(() -> trackService.addTrack(newTrack))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Duration mismatch");
    }

    @Test
    void getTrackById_shouldReturnTrack() {
        AddTrackDto newTrack = new AddTrackDto(
            "Mobile",
            "Building performant mobile apps",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "android, ios"
        );

        TrackDto addedTrack = trackService.addTrack(newTrack);

        TrackDto fetchedTrack = trackService.getTrackById(addedTrack.trackId());

        Assertions.assertThat(fetchedTrack).isNotNull();
        Assertions.assertThat(fetchedTrack.trackId()).isEqualTo(addedTrack.trackId());
        Assertions.assertThat(fetchedTrack.name()).isEqualTo(addedTrack.name());
        Assertions.assertThat(fetchedTrack.isDeleted()).isFalse();
    }

    @Test
    void getAllTracks_shouldReturnAllActiveTracks() {
        AddTrackDto track1 = new AddTrackDto(
            "Backend",
            "Building APIs",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "java, spring"
        );
        AddTrackDto track2 = new AddTrackDto(
            "Frontend",
            "Building UI",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "react, angular"
        );

        TrackDto addedTrack1 = trackService.addTrack(track1);
        TrackDto addedTrack2 = trackService.addTrack(track2);

        trackService.softDeleteTrack(addedTrack1.trackId());

        var activeTracks = trackService.getAllTracks();

        Assertions.assertThat(activeTracks)
            .hasSize(1)
            .extracting(TrackDto::trackId)
            .containsExactly(addedTrack2.trackId());
    }

    @Test
    void updateTrack_shouldUpdateFields() {
        AddTrackDto newTrack = new AddTrackDto(
            "Data Science",
            "Learning ML and data analysis",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "python, pandas, sklearn"
        );

        TrackDto addedTrack = trackService.addTrack(newTrack);

        UpdateTrackDto updateDto = new TrackDto.UpdateTrackDto(
            "Advanced Data Science",
            "Deep dive into ML and AI",
            null,
            OffsetDateTime.parse("2026-05-01T23:59:59+01:00"),
            5,
            "python, pandas, sklearn, tensorflow"
        );

        TrackDto updatedTrack = trackService.updateTrack(addedTrack.trackId(), updateDto);

        Assertions.assertThat(updatedTrack).isNotNull();
        Assertions.assertThat(updatedTrack.trackId()).isEqualTo(addedTrack.trackId());
        Assertions.assertThat(updatedTrack.name()).isEqualTo("Advanced Data Science");
        Assertions.assertThat(updatedTrack.description()).isEqualTo("Deep dive into ML and AI");
        Assertions.assertThat(updatedTrack.startDate()).isEqualTo(OffsetDateTime.parse("2025-12-01T00:00:00+01:00"));
        Assertions.assertThat(updatedTrack.duration()).isEqualTo(5);
        Assertions.assertThat(updatedTrack.learningFocus()).isEqualTo("python, pandas, sklearn, tensorflow");
    }

    @Test
    void updateTrack_withInvalidEndDate_shouldThrowBadRequestException() {
        AddTrackDto newTrack = new AddTrackDto(
            "Cloud",
            "Cloud computing fundamentals",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "aws, azure"
        );

        TrackDto addedTrack = trackService.addTrack(newTrack);

        TrackDto.UpdateTrackDto updateDto = new TrackDto.UpdateTrackDto(
            null,
            null,
            null,
            OffsetDateTime.parse("2025-11-30T23:59:59+01:00"),
            null,
            null
        );

        Assertions.assertThatThrownBy(() -> trackService.updateTrack(addedTrack.trackId(), updateDto))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("End date must be after start date");
    }

    @Test
    void updateTrack_withDurationMismatch_shouldThrowBadRequestException() {
        AddTrackDto newTrack = new AddTrackDto(
            "AI",
            "Introduction to Artificial Intelligence",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-01T23:59:59+01:00"),
            2,
            "ml, nn"
        );

        TrackDto addedTrack = trackService.addTrack(newTrack);

        TrackDto.UpdateTrackDto updateDto = new TrackDto.UpdateTrackDto(
            null,
            null,
            null,
            OffsetDateTime.parse("2026-03-01T23:59:59+01:00"),
            5,
            null
        );

        Assertions.assertThatThrownBy(() -> trackService.updateTrack(addedTrack.trackId(), updateDto))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Duration mismatch");
    }


    @Test
    void softDeleteTrack_shouldDelete() {
        AddTrackDto newTrack = new AddTrackDto(
            "QA",
            "Ensuring high quality software",
            OffsetDateTime.parse("2025-12-01T00:00:00+01:00"),
            OffsetDateTime.parse("2026-02-28T23:59:59+01:00"),
            2,
            "testing, automation"
        );

        TrackDto addedTrack = trackService.addTrack(newTrack);

        trackService.softDeleteTrack(addedTrack.trackId());

        var deletedTrack = trackRepository.findById(addedTrack.trackId())
            .orElseThrow(() -> new NotFoundException("Track not found"));

        Assertions.assertThat(deletedTrack.getIsDeleted()).isTrue();
    }
}
