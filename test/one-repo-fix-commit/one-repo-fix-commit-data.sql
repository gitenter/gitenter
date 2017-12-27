INSERT INTO config.member VALUES
	(1, 'user', 'password', 'USER', 'email@email.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 2;

INSERT INTO config.organization VALUES
	(1, 'org', 'ORG');
ALTER SEQUENCE config.organization_id_seq RESTART WITH 2;

INSERT INTO config.organization_manager_map VALUES
	(1, 1);

INSERT INTO config.repository VALUES
	(1, 1, 'repo', 'REPO', '/home/beta/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 2;

INSERT INTO git.git_commit VALUES
	(1, 1, '191f8eb03b1868e506936d0c0d3f16aad29420d5'),
	(2, 1, '0093b7e13d43c7a1b5f2037cb5c3736549dc3e78');
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;
