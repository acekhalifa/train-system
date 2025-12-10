package com.esl.academy.api.track;

import com.esl.academy.api.core.exceptions.BadRequestException;
import com.esl.academy.api.core.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.esl.academy.api.track.TrackDto.AddTrackDto;
import static com.esl.academy.api.track.TrackDto.UpdateTrackDto;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class TrackService {
    private final TrackRepository trackRepository;

    private long calculateDuration(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date must not be null");
        }
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        return ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1));
    }

    public TrackDto addTrack(@Valid AddTrackDto dto) {
        long calculatedMonths = calculateDuration(dto.startDate(), dto.endDate());

        if (calculatedMonths <= 0) {
            throw new BadRequestException("End date must be after start date");
        }

        if (dto.duration() != calculatedMonths) {
            throw new BadRequestException("Duration mismatch. Expected " + calculatedMonths + " months based on start date and end date");
        }

        Track track = Track.builder()
            .name(dto.name())
            .description(dto.description())
            .startDate(dto.startDate())
            .endDate(dto.endDate())
            .duration(dto.duration())
            .learningFocus(dto.learningFocus())
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .createdBy("{}")
            .build();

        Track saved = trackRepository.save(track);
        return TrackMapper.INSTANCE.toDto(saved);
    }

    public TrackDto updateTrack(UUID trackId, @Valid UpdateTrackDto dto) {
        Track track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));

        if (dto.name() != null) {
            track.setName(dto.name());
        }
        if (dto.description() != null) {
            track.setDescription(dto.description());
        }
        if (dto.learningFocus() != null) {
            track.setLearningFocus(dto.learningFocus());
        }

        if (dto.startDate() != null || dto.endDate() != null || dto.duration() != null) {

            // Determine the final values to use for date validation
            OffsetDateTime newStartDate = dto.startDate() != null ? dto.startDate() : track.getStartDate();
            OffsetDateTime newEndDate = dto.endDate() != null ? dto.endDate() : track.getEndDate();
            int newDuration = dto.duration() != null ? dto.duration() : track.getDuration();

            long calculatedMonths = calculateDuration(newStartDate, newEndDate);

            if (calculatedMonths <= 0) {
                throw new BadRequestException("End date must be after start date.");
            }

            if (newDuration != calculatedMonths) {
                throw new BadRequestException(
                    "Duration mismatch. Expected " + calculatedMonths + " months based on updated dates."
                );
            }

            track.setStartDate(newStartDate);
            track.setEndDate(newEndDate);
            track.setDuration(newDuration);
        }

        track.setUpdatedAt(OffsetDateTime.now());
        Track saved = trackRepository.save(track);
        return TrackMapper.INSTANCE.toDto(saved);
    }

    public TrackDto getTrackById(UUID trackId) {
        Track track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        return TrackMapper.INSTANCE.toDto(track);
    }

    public List<TrackDto> getAllTracks() {
        return trackRepository.findByIsDeletedFalse()
            .stream()
            .map(TrackMapper.INSTANCE::toDto)
            .collect(Collectors.toList());
    }

    public void softDeleteTrack(UUID trackId) {
        Track track = trackRepository.findByTrackIdAndIsDeletedFalse(trackId)
            .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));

        track.setIsDeleted(true);
        trackRepository.save(track);
    }
}
