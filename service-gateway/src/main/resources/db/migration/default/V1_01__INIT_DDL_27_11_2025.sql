-- EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ENUMS
CREATE TYPE user_type AS ENUM ('SUPER_ADMIN', 'SUPERVISOR', 'INTERN');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING');
CREATE TYPE intern_status AS ENUM ('ACTIVE', 'PENDING', 'COMPLETED', 'DISCONTINUED');
CREATE TYPE link_type AS ENUM ('SUBMISSION','LEARNING_RESOURCE');
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
    is_deleted                              BOOLEAN DEFAULT FALSE,
    name                                    VARCHAR(50),
    created_at                              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,
    CONSTRAINT pk_option_id PRIMARY KEY (option_id),
    CONSTRAINT fk_option_type FOREIGN KEY (option_type_id)
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
    CONSTRAINT uq_track_name UNIQUE (name)
);

-- USER
CREATE TABLE public."user" (
    user_id                                 UUID NOT NULL,
    first_name                              VARCHAR(50) NOT NULL,
    last_name                               VARCHAR(50) NOT NULL,
    email                                   VARCHAR(50) NOT NULL,
    password                                VARCHAR(255),
    user_type                               VARCHAR(15) NOT NULL,
    status                                  VARCHAR(15) NOT NULL,
    profile_picture_link                    TEXT,
    last_login_date                         TIMESTAMPTZ,
    login_attempts                          INT DEFAULT 0,
    created_at                              TIMESTAMPTZ DEFAULT NOW(),
    updated_at                              TIMESTAMPTZ,
    created_by                              JSON NOT NULL,
    modified_by                             JSON,
    CONSTRAINT pk_user_id PRIMARY KEY (user_id),
    CONSTRAINT ck_login_attempts CHECK (login_attempts >= 0),
    CONSTRAINT uq_user_email UNIQUE (email)
);

-- USERTYPE: intern
CREATE TABLE public.intern (
    user_id                                 UUID NOT NULL,
    track_id                                UUID NOT NULL,
    intern_status                           VARCHAR(15) NOT NULL,
    CONSTRAINT pk_intern_user_id PRIMARY KEY (user_id),
    CONSTRAINT fk_intern_user FOREIGN KEY (user_id)
        REFERENCES public."user"(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_intern_track FOREIGN KEY (track_id)
        REFERENCES public.track(track_id)
        ON DELETE RESTRICT,
    CONSTRAINT uq_intern_track UNIQUE (user_id, track_id)
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
    CONSTRAINT uq_track_month_week UNIQUE (track_id, month_id, week_id)
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
    CONSTRAINT uq_user_assessment UNIQUE (user_id, assessment_id)
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
    is_available_to_public                  BOOLEAN NOT NULL DEFAULT FALSE,
    is_check                                BOOLEAN NOT NULL DEFAULT FALSE,
    possible_values                         TEXT,
    description                             TEXT,
    updated_at                              TIMESTAMPTZ,
    modified_by                             JSON,
    CONSTRAINT pk_app_config_id PRIMARY KEY (app_config_id)
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
