CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- ENUMS
CREATE TYPE user_type AS ENUM ('SUPER_ADMIN', 'SUPERVISOR', 'INTERN');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING');
CREATE TYPE intern_status AS ENUM ('ACTIVE', 'PENDING', 'COMPLETED', 'DISCONTINUED');
CREATE TYPE link_type AS ENUM ('SUBMISSION','LEARNING_RESOURCE');


-- Assessment and submission related
CREATE TYPE publish_status AS ENUM ('PUBLISHED', 'DRAFT', 'NOT PROVIDED');
CREATE TYPE grading_status AS ENUM ('GRADED', 'NOT_GRADED', 'NOT_SUBMITTED');
CREATE TYPE submitted_status AS ENUM ('SUBMITTED', 'NOT_SUBMITTED');

-- DOMAINS
CREATE DOMAIN duration AS SMALLINT CHECK (VALUE BETWEEN 1 AND 6);
CREATE DOMAIN week AS SMALLINT CHECK (VALUE BETWEEN 1 AND 5);

-- OPTION TYPE
CREATE TABLE public.option_type (
    option_type_id                          UUID NOT NULL,
    name                                    VARCHAR(50),
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_option_type_id PRIMARY KEY (option_type_id)
);

-- OPTION
CREATE TABLE public."option" (
    option_id                               UUID NOT NULL,
    option_type_id                          UUID NOT NULL,
    parent_option_id                        UUID,
    description                             TEXT,
    name                                    VARCHAR(50),
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_option_id PRIMARY KEY (option_id),
    CONSTRAINT fk__option_type FOREIGN KEY (option_type_id)
        REFERENCES public.option_type(option_type_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_option_parent_option FOREIGN KEY (parent_option_id)
        REFERENCES public."option"(option_id)
        ON DELETE CASCADE
);
-- TRACK
CREATE TABLE public.track (
    track_id                                UUID NOT NULL,
    name                                    VARCHAR(50) NOT NULL,
    description                             TEXT,
    start_date                              DATE NOT NULL,
    end_date                                DATE NOT NULL,
    duration                                duration NOT NULL,
    is_deleted                              BOOLEAN DEFAULT FALSE,
    learning_focus                          TEXT NOT NULL,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,


    CONSTRAINT pk_track_id PRIMARY KEY (track_id),
    CONSTRAINT unique_track_name UNIQUE (name)
);

-- USER
CREATE TABLE public."user" (
    user_id                                 UUID NOT NULL,
    first_name                              VARCHAR(50) NOT NULL,
    last_name                               VARCHAR(50) NOT NULL,
    email                                   VARCHAR(50) NOT NULL,
    password_hash                           VARCHAR(255),
    user_type                               user_type NOT NULL,
    status                                  user_status NOT NULL,
    profile_picture_link                    TEXT,
    last_login                              TIMESTAMPTZ,
    login_attempts                          INT DEFAULT 0,
    created_at                              TIMESTAMPTZ DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_user_id PRIMARY KEY (user_id),
    CONSTRAINT chk_login_attempts CHECK (login_attempts >= 0),
    CONSTRAINT unique_user_email UNIQUE (email)
);

-- USERTYPE: intern
CREATE TABLE public.intern (
    user_id                                 UUID NOT NULL,
    track_id                                UUID NOT NULL,



    CONSTRAINT pk_intern_user_id PRIMARY KEY (user_id),
    CONSTRAINT fk_intern_user FOREIGN KEY (user_id)
        REFERENCES public."user"(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_intern_track FOREIGN KEY (track_id)
        REFERENCES public.track(track_id)
        ON DELETE RESTRICT,
    CONSTRAINT unique_intern_track UNIQUE (user_id, track_id)
);


-- DOCUMENT
CREATE TABLE public.document (
    document_id                             UUID NOT NULL,
    name                                    VARCHAR(255) NOT NULL,
    file_type                               varchar(10) NOT NULL,
    document_path                           TEXT NOT NULL,
    byte_size                               BIGINT NOT NULL,
    attachment                              BOOLEAN NOT NULL,
    extension                               VARCHAR(32) NOT NULL,
    extension_group                         VARCHAR(32) NOT NULL,
    is_deleted                              BOOLEAN DEFAULT FALSE,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_document_id PRIMARY KEY (document_id)
);

-- LINK
CREATE TABLE public.link (
	link_id                                 UUID NOT NULL,
	url										TEXT NOT NULL,
	title									VARCHAR(50),
	object_id								UUID NOT NULL,
	object_type 							link_type NOT NULL,




	CONSTRAINT pk_link_id PRIMARY KEY (link_id)
);

-- LEARNING RESOURCE
CREATE TABLE public.learning_resource (
    learning_resource_id                    UUID NOT NULL,
    track_id                                UUID NOT NULL,
    month_id                                UUID NOT NULL,
    week_id                                 UUID NOT NULL,
    resource_title                          VARCHAR(100) NOT NULL,
    description                             TEXT NOT NULL,
    is_deleted                              BOOLEAN DEFAULT FALSE,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_learning_resource_id PRIMARY KEY (learning_resource_id),
    CONSTRAINT fk_learning_resource_track FOREIGN KEY (track_id)
        REFERENCES public.track(track_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_lr_month FOREIGN KEY (month_id) REFERENCES public."option"(option_id),
    CONSTRAINT fk_lr_week FOREIGN KEY (week_id) REFERENCES public."option"(option_id),
    CONSTRAINT unique_track_month_week UNIQUE (track_id, month_id, week_id)
);

-- JOIN TABLE: learning_resource_document (map documents to learning_resources)
CREATE TABLE public.learning_resource_document (
	learning_resource_id                    UUID NOT NULL,
	document_id								UUID NOT NULL,



	CONSTRAINT pk_learning_document PRIMARY KEY (learning_resource_id, document_id),
	CONSTRAINT fk_learning_resource FOREIGN KEY (learning_resource_id)
		REFERENCES public.learning_resource (learning_resource_id)
		ON DELETE CASCADE,
	CONSTRAINT fk_document FOREIGN KEY (document_id)
		REFERENCES public.document (document_id)
		ON DELETE CASCADE
);

-- ASSESSMENT
CREATE TABLE public.assessment (
    assessment_id                           UUID NOT NULL,
    learning_resource_id                    UUID NOT NULL,
    description                             TEXT,
    deadline                                TIMESTAMPTZ,
    is_deleted                              BOOLEAN DEFAULT FALSE,
    published_status                        publish_status NOT NULL,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_assessment_id PRIMARY KEY (assessment_id),
    CONSTRAINT fk_learning_resource FOREIGN KEY (learning_resource_id)
        REFERENCES public.learning_resource(learning_resource_id)
        ON DELETE RESTRICT,
    CONSTRAINT unique_learning_resource UNIQUE (learning_resource_id)
);


-- SUBMISSION (join table between intern and assessment)
CREATE TABLE public.submission (
    submission_id                           UUID NOT NULL,
    user_id                                 UUID NOT NULL,
    assessment_id                           UUID NOT NULL,
    document_id								UUID,
    submission_status                       submitted_status NOT NULL,
    grading_status                          grading_status NOT NULL,
    is_deleted                              BOOLEAN DEFAULT FALSE,
    feedback                                TEXT,
    submission_note                         TEXT,
    score                                   INT NOT NULL DEFAULT 0,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,



    CONSTRAINT pk_submission_id PRIMARY KEY (submission_id),
    CONSTRAINT fk_submission_user FOREIGN KEY (user_id)
        REFERENCES public.intern(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_submission_assessment FOREIGN KEY (assessment_id)
        REFERENCES public.assessment(assessment_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_submission_document FOREIGN KEY (document_id)
        REFERENCES public.document(document_id)
        ON DELETE SET NULL,
    CONSTRAINT unique_user_assessment UNIQUE (user_id, assessment_id)
);

-- JOIN TABLE: supervisor_track (map supervisors to tracks)
CREATE TABLE public.supervisor_track (
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

-- APP CONFIG
CREATE TABLE public.app_config (
    app_config_id                           VARCHAR,
    app_config_value                        TEXT NOT NULL,
    is_available_to_public                  BOOLEAN NOT NULL,
    is_check                                BOOLEAN NOT NULL,
    possible_values                         TEXT,
    description                             TEXT,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON
);

CREATE TABLE public.notification (
    notification_id                         UUID NOT NULL,
    user_id                                 UUID NOT NULL,
    title                                   VARCHAR(255) NOT NULL,
    message                                 TEXT,
    is_read                                 BOOLEAN DEFAULT FALSE,
    active                                  BOOLEAN DEFAULT TRUE,
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    read_at                                 TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,

    CONSTRAINT pk_notification_id PRIMARY KEY (notification_id),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id)
        REFERENCES public."user"(user_id)
        ON DELETE CASCADE
);







-- ===============================================
-- TRIGGER FUNCTIONS TO ENFORCE USER ROLES
-- ===============================================

-- TRIGGER FUNCTION: enforce intern type
CREATE OR REPLACE FUNCTION check_is_user_intern()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT user_type FROM public."user" WHERE user_id = NEW.user_id) <> 'intern' THEN
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGERS
CREATE TRIGGER trg_check_is_user_intern
BEFORE INSERT OR UPDATE ON public.intern
FOR EACH ROW
EXECUTE FUNCTION check_is_user_intern();




-- ===============================================
--DATABASE VIEWS
-- ===============================================

-- VIEW: Submission List
CREATE OR REPLACE VIEW public.vw_ui_submission_list AS
SELECT
    s.submission_id,
    u.first_name || ' ' || u.last_name AS intern_name,
    u.profile_picture_link,
    t.name AS track_name,
    lr.resource_title AS task_title,
    s.submission_status,
    s.grading_status,
    s.created_at AS submitted_on,
    s.user_id AS intern_user_id,
    s.assessment_id
FROM public.submission s
JOIN public.intern i ON s.user_id = i.user_id
JOIN public."user" u ON i.user_id = u.user_id
JOIN public.track t ON i.track_id = t.track_id
JOIN public.assessment a ON s.assessment_id = a.assessment_id
JOIN public.learning_resource lr ON a.learning_resource_id = lr.learning_resource_id;

-- VIEW: Track Stats
CREATE OR REPLACE VIEW public.vw_ui_track_stats AS
SELECT
    t.track_id,
    t.name AS track_name,
    t.duration || ' Months' AS duration_label,
    (SELECT COUNT(*) FROM public.intern i WHERE i.track_id = t.track_id) AS no_of_students,
    (SELECT COUNT(*) FROM public.supervisor_track st WHERE st.track_id = t.track_id) AS no_of_supervisors,
    t.created_at
FROM public.track t
WHERE t.is_deleted = FALSE;

-- VIEW: Supervisor List
CREATE OR REPLACE VIEW public.vw_ui_supervisor_list AS
SELECT
    u.user_id,
    u.first_name || ' ' || u.last_name AS name,
    u.email,
    u.status,
    STRING_AGG(DISTINCT t.name, ', ') AS assigned_tracks,
    (
        SELECT COUNT(DISTINCT i.user_id)
        FROM public.intern i
        JOIN public.supervisor_track st_sub ON i.track_id = st_sub.track_id
        WHERE st_sub.user_id = u.user_id
    ) AS no_of_interns
FROM public."user" u
JOIN public.supervisor_track st ON u.user_id = st.user_id
JOIN public.track t ON st.track_id = t.track_id
GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.status;

-- VIEW: Intern List
CREATE OR REPLACE VIEW public.vw_ui_intern_list AS
SELECT
    i.user_id,
    u.first_name || ' ' || u.last_name AS name,
    u.email,
    u.status AS user_status,
    t.name AS track_name,
    t.start_date,
    t.end_date,
    (
        SELECT STRING_AGG(DISTINCT u_sup.first_name || ' ' || u_sup.last_name, ', ')
        FROM public.supervisor_track st
        JOIN public."user" u_sup ON st.user_id = u_sup.user_id
        WHERE st.track_id = t.track_id
    ) AS supervisors
FROM public.intern i
JOIN public."user" u ON i.user_id = u.user_id
JOIN public.track t ON i.track_id = t.track_id;

-- 5. LEARNING RESOURCES VIEW

CREATE OR REPLACE VIEW public.vw_ui_resource_list AS
SELECT
    lr.learning_resource_id,
    m.name AS month_name,
    w.name AS week_name,
    t.name AS track_name,
    lr.resource_title,
    lr.created_at AS date_created

FROM public.learning_resource lr
JOIN public.track t ON lr.track_id = t.track_id
-- Join Month Option
JOIN public.option m ON lr.month_id = m.option_id
-- Join Week Option
JOIN public.option w ON lr.week_id = w.option_id
WHERE lr.is_deleted = FALSE;


-- 6. ASSESSMENT LIST VIEW
CREATE OR REPLACE VIEW public.vw_ui_assessment_list AS
SELECT
    a.assessment_id,
    m.name AS month_name,
    w.name AS week_name,
    t.name AS track_name,
    lr.resource_title AS task_title,
    a.deadline,
    a.published_status
FROM public.assessment a
JOIN public.learning_resource lr ON a.learning_resource_id = lr.learning_resource_id
JOIN public.track t ON lr.track_id = t.track_id
JOIN public.option m ON lr.month_id = m.option_id
JOIN public.option w ON lr.week_id = w.option_id
WHERE a.is_deleted = FALSE;


-- Indexes

--SUPERVISOR & INTERN FILTERS
CREATE INDEX idx_user_names ON public."user" (first_name, last_name);
CREATE INDEX idx_track_dates ON public.track (start_date, end_date);
CREATE INDEX idx_intern_track_id ON public.intern (track_id);

--LEARNING RESOURCE
CREATE INDEX idx_lr_track_month_week ON public.learning_resource (track_id, month_id, week_id);

--ASSESSMENT & SUBMISSION
CREATE INDEX idx_assessment_resource ON public.assessment (learning_resource_id);
CREATE INDEX idx_submission_grading_status ON public.submission (submission_status, grading_status);
CREATE INDEX idx_submission_user ON public.submission (user_id);
CREATE INDEX idx_submission_assessment ON public.submission (assessment_id);

--SUBMISSIONS
CREATE INDEX idx_submission_status_only ON public.submission (submission_status);
