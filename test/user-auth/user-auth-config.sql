\set username user_auth
\set password zooo
\set dbname user_auth

SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = ':dbname';

DROP DATABASE IF EXISTS :dbname;

DROP USER IF EXISTS :username;

CREATE USER :username CREATEDB PASSWORD ':password';

CREATE DATABASE :dbname OWNER :username;
