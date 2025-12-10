-- ===============================================
-- SUPER_ADMIN USER
-- Password hash corresponds to password123
-- ===============================================
INSERT INTO public."user"
(user_id, first_name, last_name, email, password_hash, user_type, status, created_by)
VALUES
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Super', 'Admin', 'admin@example.com',
 '$2a$10$IOJSoGzq8UtuSOGKp8UeYuns1dMq0P9Q0kM8rQZ6OY0GJE0E7E1Tu',
 'SUPER_ADMIN', 'ACTIVE', '{"system":"init"}');

-- ===============================================
-- APP CONFIGS
-- ===============================================
INSERT INTO public.app_config
(app_config_id, app_config_value, is_available_to_public, is_check, possible_values, description, created_by)
VALUES
('MAX_LOGIN_ATTEMPTS', '5', FALSE, FALSE, NULL, 'Maximum password login attempts', '{"system": "init"}'),
('FILE_BYTE_UPLOAD_LIMIT', '10485760', FALSE, FALSE, NULL, 'Max file upload size in bytes (10 MB)', '{"system": "init"}');

-- ===============================================
-- OPTION TYPES
-- ===============================================
INSERT INTO public.option_type (option_type_id, name, created_by)
VALUES
(gen_random_uuid(), 'month', '{"system":"seed"}'),
(gen_random_uuid(), 'week', '{"system":"seed"}');

-- ===============================================
-- MONTH OPTIONS
-- ===============================================
INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'january', 'January', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'february', 'February', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'march', 'March', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'april', 'April', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'may', 'May', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'june', 'June', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'july', 'July', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'august', 'August', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'september', 'September', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'october', 'October', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'november', 'November', '{"system":"init"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, 'december', 'December', '{"system":"init"}'
FROM public.option_type WHERE name='month';

-- ===============================================
-- WEEK OPTIONS
-- ===============================================
INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, '1', 'Week 1', '{"system":"seed"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, '2', 'Week 2', '{"system":"seed"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, '3', 'Week 3', '{"system":"seed"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, '4', 'Week 4', '{"system":"seed"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT gen_random_uuid(), option_type_id, '5', 'Week 5', '{"system":"seed"}'
FROM public.option_type WHERE name='week';
