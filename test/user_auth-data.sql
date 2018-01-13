INSERT INTO config.member VALUES
	(1, 'user1', 'password1', 'USER1', 'user1@user1.com'),
	(2, 'user2', 'password2', 'USER2', 'user2@user2.com'),
	(3, 'user3', 'password3', 'USER3', 'user3@user3.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 4;

INSERT INTO config.organization VALUES
	(1, 'org1', 'ORG1'),
	(2, 'org2', 'ORG2'),
	(3, 'org3', 'ORG3');
ALTER SEQUENCE config.organization_id_seq RESTART WITH 4;

INSERT INTO config.organization_manager_map VALUES
	(1, 1),
	(1, 2),
	(2, 2),
	(2, 3),
	(3, 3);

INSERT INTO config.repository VALUES
	(1, 1, 'repo1', 'REPO1', '~/org1/repo1.git'),
	(2, 1, 'repo2', 'REPO2', '~/org1/repo2.git'),
	(3, 2, 'repo1', 'REPO1', '~/org2/repo1.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 4;
