package com.esl.academy.api.track;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import static com.esl.academy.api.track.TrackDto.AddTrackDto;
import static com.esl.academy.api.track.TrackDto.UpdateTrackDto;

@Tag(name = "Track")
@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class TrackController {
    private final TrackService trackService;

    @Operation(summary = "Add a track")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackDto addTrack(@RequestBody @Valid AddTrackDto dto) {
        return trackService.addTrack(dto);
    }

    @Operation(summary = "Update a track")
    @PatchMapping("/{trackId}")
    public TrackDto updateTrack(
        @PathVariable UUID trackId,
        @Valid @RequestBody UpdateTrackDto dto
    ) {
        return trackService.updateTrack(trackId, dto);
    }

    @Operation(summary = "Fetch a track by id")
    @GetMapping("/{trackId}")
    public TrackDto getTrackById(@PathVariable UUID trackId) {
        return trackService.getTrackById(trackId);
    }

    @Operation(summary = "Fetch all tracks")
    @GetMapping
    public List<TrackDto> getAllTracks() {
        return trackService.getAllTracks();
    }

    @Operation(summary = "Delete a track")
    @DeleteMapping("/{trackId}")
    public void softDeleteTrack(UUID trackId) {
        trackService.softDeleteTrack(trackId);
    }
}
