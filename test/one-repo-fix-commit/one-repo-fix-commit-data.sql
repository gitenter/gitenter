INSERT INTO config.member VALUES
	(1, 'one-repo-fix-commit-username', 'one-repo-fix-commit-password', 'ONE-REPO-FIX-COMMIT-USERNAME', 'email@email.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 2;

INSERT INTO config.organization VALUES
	(1, 'one-repo-fix-commit-org', 'ONE-REPO-FIX-COMMIT-ORG'),
ALTER SEQUENCE config.organization_id_seq RESTART WITH 2;

INSERT INTO config.organization_manager_map VALUES
	(1, 1);

INSERT INTO config.repository VALUES
	(1, 1, 'one-repo-fix-commit-repo', 'ONE-REPO-FIX-COMMIT-REPO', '/home/beta/Workspace/enterovirus-test/one-repo-fix-commit.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 2;

INSERT INTO git.git_commit VALUES
	(1, 1, 'a039685c020d73cdbdd30dedbb89a6b262bca793'),
	(1, 1, 'f78ebcf9c6b4c998d9682c3e089abcf2c0f56dee'),
ALTER SEQUENCE git.git_commit_id_seq RESTART WITH 3;

INSERT INTO git.document VALUES
	(1, 6),
	(2, 6),
	(3, 7),
	(4, 7),
	(5, 7);

INSERT INTO git.document_modified VALUES
	(1, 'folder_1/same-name-file'),
	(2, 'test-add-a-file-from-client_1'),
	(3, 'folder_2/same-name-file'),
	(4, 'test-add-a-file-from-client_1');

INSERT INTO git.document_unmodified VALUES
	(5, 1);