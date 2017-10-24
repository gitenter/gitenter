INSERT INTO config.member VALUES
	(1, 'ann', 'aaa', 'Ann Author', 'ann@ann.com'),
	(2, 'bell', 'bbb', 'Bell Author', 'bell@bell.com'),
	(3, 'cindy', 'ccc', NULL, 'cindy@cindy.com');
ALTER SEQUENCE config.member_id_seq RESTART WITH 4;

INSERT INTO config.organization VALUES
	(1, 'gov', 'Government'),
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
	(1, 1, 'aaa', 'AAA', 'https://git.com/gov/aaa.git'),
	(2, 1, 'bbb', NULL, 'https://git.com/gov/bbb.git'),
	(3, 2, 'aaa', 'AAA', 'https://git.com/ngo/aaa.git');
ALTER SEQUENCE config.repository_id_seq RESTART WITH 4;