ALTER TABLE public.track
    DROP COLUMN start_date,
    DROP COLUMN end_date;

ALTER TABLE public.track
    ALTER COLUMN duration TYPE int
    USING duration::int;

ALTER TABLE public."user"
    ADD COLUMN address TEXT,
    ADD COLUMN phone_number BIGINT;
