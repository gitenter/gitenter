SELECT pg_terminate_backend (pg_stat_activity.pid)
	FROM pg_stat_activity
	WHERE pg_stat_activity.datname = 'one_repo_fix_commit';

DROP DATABASE IF EXISTS one_repo_fix_commit;

DROP USER IF EXISTS enterovirus_test;

CREATE USER enterovirus_test CREATEDB PASSWORD 'zooo';

CREATE DATABASE one_repo_fix_commit OWNER enterovirus_test;
