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
	(1, 1, '7be10ee268b2d4445fdc4732ef4023cf12c5ce95'),
	(2, 1, 'fac8463c1cca31d1333b5b91b0b4ecebae7e9512');
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;

INSERT INTO git.git_commit_valid VALUES
	(1),
	(2);
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;

INSERT INTO git.document VALUES
	(1, 1, '1st-commit-file-under-root'),
	(2, 1, '1st-commit-file-to-be-change-in-the-2nd-commit'),
	(3, 1, '1st-commit-folder/1st-commit-file-under-1st-commit-folder'),
	(4, 2, '1st-commit-file-under-root'),
	(5, 2, '1st-commit-file-to-be-change-in-the-2nd-commit'),
	(6, 2, '1st-commit-folder/1st-commit-file-under-1st-commit-folder'),
	(7, 2, '2nd-commit-file-under-root'),
	(8, 2, '1st-commit-folder/2nd-commit-file-under-1st-commit-folder'),
	(9, 2, '2nd-commit-folder/2nd-commit-file-under-2nt-commit-folder');
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 10;
