-- Rename the enum type from submitted_status to submission_status
ALTER TYPE submitted_status RENAME TO submission_status;

-- Update the column definition in the submission table to reference the new name
ALTER TABLE public.submission
ALTER COLUMN submission_status TYPE submission_status
    USING submission_status::text::submission_status;

DROP TABLE public.supervisor_track;

ALTER TABLE public."user" ADD COLUMN track_id UUID;

-- SUBMISSION DOCUMENT
CREATE TABLE public.submission_document (
    submission_id                           UUID NOT NULL,
    document_id                             UUID NOT NULL,
    CONSTRAINT pk_submission_document PRIMARY KEY (submission_id, document_id),
    CONSTRAINT fk_submission FOREIGN KEY (submission_id)
        REFERENCES submission(submission_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_document FOREIGN KEY (document_id)
        REFERENCES document(document_id)
        ON DELETE CASCADE
);
