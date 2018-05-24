INSERT INTO settings.member VALUES
	(1, 'user', 'password', 'USER', 'email@email.com');
ALTER SEQUENCE settings.member_id_seq RESTART WITH 2;

INSERT INTO settings.organization VALUES
	(1, 'org', 'ORG');
ALTER SEQUENCE settings.organization_id_seq RESTART WITH 2;

INSERT INTO settings.organization_manager_map VALUES
	(1, 1);

INSERT INTO settings.repository VALUES
	(1, 1, 'repo', 'REPO', NULL, TRUE);
ALTER SEQUENCE settings.repository_id_seq RESTART WITH 2;

-- The git.git_commit table doesn't need to be pre-filled,
-- because how to fillin is the part need to be tested.
