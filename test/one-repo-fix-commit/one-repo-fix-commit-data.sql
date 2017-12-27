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
	(1, 1, '86f8e569acf1e989fcaccaf0c744e308761ebf64'),
	(2, 1, '427dffe30c6b3e9791c44843075a6a529d101352');
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;

INSERT INTO git.document VALUES
	(1, 1),
	(2, 1),
	(3, 1),
	(4, 2),
	(5, 2),
	(6, 2),
	(7, 2),
	(8, 2),
	(9, 2);
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 10;

INSERT INTO git.document_modified VALUES
	(1, '1st-commit-file-under-root'),
	(2, '1st-commit-file-to-be-change-in-the-2nd-commit'),
	(3, '1st-commit-folder/1st-commit-file-under-1st-commit-folder'),
	(5, '1st-commit-file-to-be-change-in-the-2nd-commit'),
	(7, '2nd-commit-file-under-root'),
	(8, '1st-commit-folder/2nd-commit-file-under-1st-commit-folder'),
	(9, '2nd-commit-folder/2nd-commit-file-under-2nt-commit-folder');

INSERT INTO git.document_unmodified VALUES
	(4, 1),
	(6, 3);
