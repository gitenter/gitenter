\set username hook_update_client_side_fake_username
\set password hook_update_client_side_fake_password
\set dbname hook_update_client_side_fake_dbname

SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = ':dbname';

DROP DATABASE IF EXISTS :dbname;

DROP USER IF EXISTS :username;

-- Password cannot be given by SQL variable ':password'
-- I don't understand why
CREATE USER :username CREATEDB PASSWORD 'hook_update_client_side_fake_password';
--CREATE USER :username CREATEDB PASSWORD ':password';

CREATE DATABASE :dbname OWNER :username;
