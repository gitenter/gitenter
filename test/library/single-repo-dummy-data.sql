INSERT INTO config.member VALUES
	(1, 'user', 'password', 'USER', 'email@email.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 2;

INSERT INTO config.organization VALUES
	(1, 'org', 'ORG');
ALTER SEQUENCE config.organization_id_seq RESTART WITH 2;

INSERT INTO config.organization_manager_map VALUES
	(1, 1);

INSERT INTO config.repository VALUES
	(1, 1, 'repo', 'REPO', '/not/in/use');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 2;

-- The git.git_commit table doesn't need to be pre-filled,
-- because how to fillin is the part need to be tested.
