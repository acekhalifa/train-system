ALTER TABLE public.track
DROP CONSTRAINT IF EXISTS uq_track_name;

CREATE UNIQUE INDEX uq_track_name_active
ON public.track (name)
WHERE (is_deleted IS FALSE);
