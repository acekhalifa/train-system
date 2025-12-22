--INSERT INTO public.app_config
--(app_config_id, app_config_value, is_available_to_public, is_check, possible_values, description, created_by)
--VALUES
--('MAX_LOGIN_ATTEMPTS', '10485760', FALSE, FALSE, NULL, 'Max file upload size in bytes (10 MB)', '{"system": "init"}'),
--('FILE_BYTE_UPLOAD_LIMIT', '5', FALSE, FALSE, NULL, 'Maximum password login attempts', '{"system": "init"}'));
--
--INSERT INTO public.option_type (option_type_id, name, created_by)
--VALUES (uuid_generate_v4(), 'week', '{"system":"seed"}');
--
--INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
--SELECT
--    uuid_generate_v4(),
--    (SELECT option_type_id FROM public.option_type WHERE name = 'week' LIMIT 1),
--    w,
--    'Week '||w,
--    '{"system":"seed"}'
--FROM (VALUES ('1'), ('2'), ('3'), ('4'), ('5')) AS t(w);


INSERT INTO public.app_config (app_config_id, app_config_value)
VALUES ('INVITATION_EXPIRY_DAYS', '2')
ON CONFLICT (app_config_id)
DO UPDATE SET
    app_config_value = EXCLUDED.app_config_value;


INSERT INTO public.document (
    document_id,
    name,
    file_type,
    document_path,
    byte_size,
    attachment,
    extension,
    extension_group,
    created_by
)
VALUES (
    '9c2c6d90-1d27-4b3b-9f1f-5cdb6f1c9999',
    'Backend Track Certificate',
    'PDF',
    '/certificates/backend-track.pdf',
    204800,
    TRUE,
    'pdf',
    'application',
    '{"system":"flyway-test"}'
);

INSERT INTO public.track (
    track_id,
    name,
    description,
    duration,
    is_deleted,
    learning_focus,
    created_by
)
VALUES (
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Backend',
    'Master APIs and Databases',
    3,
    FALSE,
    'APIs, databases, Java',
    '{"system": "test_user"}'
);

INSERT INTO public."user" (
    user_id,
    first_name,
    last_name,
    email,
    user_type,
    status,
    created_at,
    created_by
) VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'John',
    'Doe',
    'john@example.com',
    'SUPER_ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
), (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Jane',
    'Smith',
    'jane@example.com',
    'SUPERVISOR',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
), (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Mikky',
    'Fire',
    'mikky@example.com',
    'INTERN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
);

INSERT INTO public.intern (
    user_id,
    track_id,
    intern_status
) VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    'ACTIVE'
);

-- ===============================================
-- LEARNING RESOURCE TEST DATA
-- ===============================================

INSERT INTO public.learning_resource (
    learning_resource_id,
    track_id,
    month_id,
    week_id,
    resource_title,
    description,
    is_deleted,
    created_at,
    created_by
) VALUES
(
    'b4c46104-dac7-430d-9e22-e8285d192974',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12', -- Backend track
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='month' AND o.name='january'),
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='week' AND o.name='1'),
    'REST API Basics',
    'Introduction to building RESTful APIs with Java and Spring Boot.',
    FALSE,
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
),
(
    '1c1711c5-b7ff-4133-b79e-dfd6a49708cd',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='month' AND o.name='february'),
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='week' AND o.name='2'),
    'Spring Boot Dependency Injection',
    'Learn how to manage beans and dependencies in Spring Boot.',
    FALSE,
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
),
(
    'c7586054-d157-4283-9864-030e7d43744b',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='month' AND o.name='march'),
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='week' AND o.name='3'),
    'Database Design',
    'Introduction to designing relational databases for backend applications.',
    FALSE,
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
),
(
    'ad416edd-0533-4ea2-9d7f-bc3495348a3c',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='month' AND o.name='april'),
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='week' AND o.name='4'),
    'REST Security',
    'Learn about authentication and authorization in RESTful APIs.',
    FALSE,
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
),
(
    '7a13340c-e57f-47f5-acc4-f2e78e94f2e2',
    'c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12',
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='month' AND o.name='may'),
    (SELECT option_id FROM public.option o JOIN public.option_type ot ON o.option_type_id = ot.option_type_id WHERE ot.name='week' AND o.name='5'),
    'Advanced JPA',
    'Deep dive into JPA mappings, relationships, and performance optimizations.',
    FALSE,
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
);

INSERT INTO public.assessment (
    assessment_id,
    learning_resource_id,
    description,
    deadline,
    is_deleted,
    published_status,
    created_at,
    created_by
) VALUES (
    'b4f46104-dab7-420d-9e22-f9285d192974',
    'b4c46104-dac7-430d-9e22-e8285d192974',
    'An assessment description',
    '2025-12-12T23:59:00+01:00',
    FALSE,
    'PUBLISHED',
    CURRENT_TIMESTAMP,
    '{"system": "test_user"}'
),
(
    '1d9f2632-6fcd-4b8a-818d-8b98af87bfb1',
    '1c1711c5-b7ff-4133-b79e-dfd6a49708cd',
    'Test assessment',
    NOW() + INTERVAL '7 day',
    FALSE,
    'DRAFT',
    CURRENT_TIMESTAMP,
    '{"userId": "11111111-1111-1111-1111-111111111111", "email": "test@user.com"}'
);
