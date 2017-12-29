\set username long_commit_path_username
\set password long_commit_path_password
\set dbname long_commit_path_dbname

SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = ':dbname';

DROP DATABASE IF EXISTS :dbname;

DROP USER IF EXISTS :username;

-- Password cannot be given by SQL variable ':password'
-- I don't understand why
CREATE USER :username CREATEDB PASSWORD 'long_commit_path_password';
--CREATE USER :username CREATEDB PASSWORD ':password';

CREATE DATABASE :dbname OWNER :username;
