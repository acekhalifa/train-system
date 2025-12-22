CREATE TABLE IF NOT EXISTS public.event_publication (
    id                                          UUID NOT NULL,
    listener_id                                 TEXT NOT NULL,
    event_type                                  TEXT NOT NULL,
    serialized_event                            TEXT NOT NULL,
    publication_date                            TIMESTAMPTZ NOT NULL,
    completion_date                             TIMESTAMPTZ,
    CONSTRAINT pk_event_publication PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_event_publication_by_completion_date
    ON public.event_publication (completion_date);

CREATE INDEX IF NOT EXISTS idx_event_publication_serialized_event
    ON public.event_publication (serialized_event);
