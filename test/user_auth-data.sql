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
	(1, 1, 'repo1', 'REPO1', NULL, TRUE),
	(2, 1, 'repo2', 'REPO2', NULL, TRUE),
	(3, 2, 'repo1', 'REPO1', NULL, TRUE);
ALTER SEQUENCE config.repository_id_seq RESTART WITH 4;

INSERT INTO config.repository_member_map VALUES
	(1, 1, 1, 'L'),
	(2, 1, 2, 'E'),
	(3, 1, 3, 'V'),
	(4, 2, 1, 'L'),
	(5, 3, 1, 'E');
ALTER SEQUENCE config.repository_member_map_id_seq RESTART WITH 6;
