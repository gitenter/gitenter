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

-- The git.git_commit table doesn't need to be pre-filled,
-- because how to fillin is the part need to be tested.
--INSERT INTO git.git_commit VALUES
--	(1, 1, '86f8e569acf1e989fcaccaf0c744e308761ebf64'),
--	(2, 1, '427dffe30c6b3e9791c44843075a6a529d101352');
--ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;
