INSERT INTO config.member VALUES
	(1, 'ann', 'aaa', 'Ann Author', 'ann@ann.com'),
	(2, 'bell', 'bbb', 'Bell Author', 'bell@bell.com'),
	(3, 'cindy', 'ccc', NULL, 'cindy@cindy.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 4;

INSERT INTO config.organization VALUES
	(1, 'user1', 'Government'),
	(2, 'ngo', 'Non-governmental organization'),
	(3, 'individual', NULL);
ALTER SEQUENCE config.organization_id_seq RESTART WITH 4;

INSERT INTO config.organization_manager_map VALUES
	(1, 1),
	(1, 2),
	(2, 2),
	(2, 3),
	(3, 3);

INSERT INTO config.repository VALUES
	(1, 1, 'repo1', 'AAA', '/home/beta/user1/repo1/.git'),
	(2, 1, 'bbb', NULL, 'https://git.com/gov/bbb.git'),
	(3, 2, 'aaa', 'AAA', 'https://git.com/ngo/aaa.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 4;

INSERT INTO git.git_commit VALUES
	(1, 1, 'c3474227d51ed985a4bf12c3099a68d6dbc11a77'),
	(2, 1, '834a67585dcd0a83e09e8fa34a6741bad1f0be73'),
	(3, 1, '86e9b06f5ebd285eaa2dcef1ba10451cbe8037e9'),
	(4, 1, 'a1ee78d350f2b5f92311bcf3d008b07b943f94ac'),
	(5, 1, 'ac211df0fbe5e2368ba82f1c26a1f3aab192fc35'),
	(6, 1, 'ff728f5674201025b9fc4ea76a0adde3323fb9fb'),
	(7, 1, '841d9d8cb6c560f1efc4ff677b8c71362d71203c');

INSERT INTO git.document VALUES
	(1, 6),
	(2, 6),
	(3, 7),
	(4, 7);

INSERT INTO git.modified_document VALUES
	(1, 'folder_1/same-name-file'),
	(2, 'test-add-a-file-from-client_1'),
	(3, 'folder_1/same-name-file'),
	(4, 'test-add-a-file-from-client_1');
