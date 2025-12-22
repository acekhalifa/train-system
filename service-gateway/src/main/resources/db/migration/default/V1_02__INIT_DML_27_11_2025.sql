-- ===============================================
-- SUPER_ADMIN USER
-- Password hash corresponds to Password2024!
-- ===============================================
INSERT INTO public."user"
(user_id, first_name, last_name, email, password, user_type, status, created_by)
VALUES
(uuid_generate_v4(), 'TCMP', 'Admin', 'admin@tcmp.com', '$2a$10$D/3Ai1G3gHFqliZQe9MTne2SLKqd1K32Fdg4CiASCGZ.vZFQJjtN6', 'SUPER_ADMIN', 'ACTIVE', '{"userId": "system","username":"system","name":"system"}');

-- ===============================================
-- APP CONFIGS
-- ===============================================
INSERT INTO public.app_config(app_config_id, app_config_value, description)
VALUES
('MAX_LOGIN_ATTEMPTS', '5', 'Maximum password login attempts'),
('FILE_BYTE_UPLOAD_LIMIT', '10485760', 'Max file upload size in bytes (10 MB)'),
('INVITATION_EXPIRY_DAYS', '3', 'Number of days before an invitation link expires'),
('ACCOUNT_ACTIVATION_DELAY_AFTER_MAX_LOGIN_ATTEMPTS', '1800', 'How long it takes (in seconds) for a user to wait before they can attempt to login after temporary account deactivation due to maximum invalid login attempts usage.');

-- ===============================================
-- OPTION TYPES
-- ===============================================
INSERT INTO public.option_type (option_type_id, name, created_by)
VALUES
(uuid_generate_v4(), 'month', '{"userId": "system","username":"system","name":"system"}'),
(uuid_generate_v4(), 'week', '{"userId": "system","username":"system","name":"system"}');

-- ===============================================
-- MONTH OPTIONS
-- ===============================================
INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'january', 'January', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'february', 'February', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'march', 'March', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'april', 'April', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'may', 'May', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'june', 'June', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'july', 'July', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'august', 'August', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'september', 'September', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'october', 'October', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'november', 'November', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, 'december', 'December', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='month';

-- ===============================================
-- WEEK OPTIONS
-- ===============================================
INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, '1', 'Week 1', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, '2', 'Week 2', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, '3', 'Week 3', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, '4', 'Week 4', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='week';

INSERT INTO public.option (option_id, option_type_id, name, description, created_by)
SELECT uuid_generate_v4(), option_type_id, '5', 'Week 5', '{"userId": "system","username":"system","name":"system"}'
FROM public.option_type WHERE name='week';
