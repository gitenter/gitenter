\set username user_auth_username
--\set password user_auth_password
\set dbname user_auth_dbname

SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = ':dbname';

DROP DATABASE IF EXISTS :dbname;

DROP USER IF EXISTS :username;

--CREATE USER :username CREATEDB PASSWORD ':password';
CREATE USER :username CREATEDB PASSWORD 'user_auth_password';

CREATE DATABASE :dbname OWNER :username;
