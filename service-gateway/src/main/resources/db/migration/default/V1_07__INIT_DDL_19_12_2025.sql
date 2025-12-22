CREATE TABLE IF NOT EXISTS public.supervisor_track (
    user_id                                 UUID NOT NULL,
    track_id                                UUID NOT NULL,
    CONSTRAINT pk_supervisor_track PRIMARY KEY (user_id, track_id),
    CONSTRAINT fk_supervisor FOREIGN KEY (user_id)
        REFERENCES public."user"(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_supervisor_track FOREIGN KEY (track_id)
        REFERENCES public.track(track_id)
        ON DELETE CASCADE
);
