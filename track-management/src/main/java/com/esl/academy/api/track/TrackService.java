package com.esl.academy.api.track;

import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.event.TrackCreatedEvent;
import com.esl.academy.api.relationship.SupervisorTrack;
import com.esl.academy.api.user.Supervisor;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.esl.academy.api.track.TrackDto.AddTrackDto;
import static com.esl.academy.api.track.TrackDto.UpdateTrackDto;

@Service
@RequiredArgsConstructor
@Validated
public class TrackService {

    private final TrackRepository trackRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public TrackDto addTrack(AddTrackDto dto) {

        final var track = Track.builder()
            .name(dto.name())
            .description(dto.description())
            .duration(dto.duration())
            .learningFocus(dto.learningFocus())
            .isDeleted(false)
            .build();

        trackRepository.save(track);

        applicationEventPublisher.publishEvent(
            new TrackCreatedEvent(track)
        );

        return TrackMapper.INSTANCE.map(track);
    }

    @Transactional
    public TrackDto updateTrack(UUID trackId, UpdateTrackDto dto) {
        final var track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));

        if (dto.name() != null) {
            track.setName(dto.name());
        }
        if (dto.description() != null) {
            track.setDescription(dto.description());
        }
        if (dto.learningFocus() != null) {
            track.setLearningFocus(dto.learningFocus());
        }
        if (dto.duration() != null) {
            track.setDuration(dto.duration());
        }

        track.setUpdatedAt(OffsetDateTime.now());
        final var saved = trackRepository.save(track);
        return TrackMapper.INSTANCE.map(saved);
    }

    public TrackDto getTrackById(UUID trackId) {
        final var track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));
        return TrackMapper.INSTANCE.map(track);
    }


    public Page<TrackDto> getAllTracks(Pageable pageable) {
        return trackRepository.findByIsDeletedFalse(pageable).map(TrackMapper.INSTANCE::map);
    }

    @Transactional
    public void softDeleteTrack(UUID trackId) {
        final var track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));

        track.setDeleted(true);
        trackRepository.save(track);
    }

    public Set<Supervisor> getAllSupervisorsForTrack(UUID trackId) {
        final var track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found"));
        return track.getSupervisors().stream()
            .map(SupervisorTrack::getSupervisor)
            .collect(Collectors.toUnmodifiableSet());
    }

    public String getTrackNameById(UUID trackId) {
        return trackRepository.findById(trackId)
            .map(Track::getName)
            .orElseThrow(() -> new NotFoundException("Track not found"));
    }

    public TrackDto getTrackByName(@NotNull String name) {
        return trackRepository.findByNameAndIsDeletedFalse(name)
            .map(TrackMapper.INSTANCE::map)
            .orElseThrow(() -> new NotFoundException("Track not found"));
    }
}
