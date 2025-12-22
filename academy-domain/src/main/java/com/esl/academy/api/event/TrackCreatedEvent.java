package com.esl.academy.api.event;

import com.esl.academy.api.track.Track;

public record TrackCreatedEvent(
    Track track
) {
}
