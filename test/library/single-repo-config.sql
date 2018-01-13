SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = ':dbname';

DROP DATABASE IF EXISTS :dbname;

DROP USER IF EXISTS :username;

-- Password cannot be given by SQL variable ':password'.
-- I don't understand why. So right now I setup everybody
-- with password 'postgres'.
--CREATE USER :username CREATEDB PASSWORD 'one_commit_traceability_password';
--CREATE USER :username CREATEDB PASSWORD ':password';
CREATE USER :username WITH PASSWORD 'postgres';

CREATE DATABASE :dbname OWNER :username;
