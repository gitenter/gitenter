CREATE TABLE review.milestone (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE RESTRICT,
	name text NOT NULL,
	description text
);

CREATE TABLE setting.review_status (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

INSERT INTO setting.review_status VALUES
	(1, 'review'),
	(2, 'approval'),
	(3, 'denial');

CREATE TABLE review.review_document (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE RESTRICT,

	milestone_id serial REFERENCES review.milestone (id) ON DELETE CASCADE,
	CHECK (milestone_id = git.commit_id_from_document(id)),

	status serial REFERENCES setting.review_status (id) ON DELETE RESTRICT --DEFAULT 1
);

CREATE TABLE review.issue (
	id serial PRIMARY KEY,

	member_id serial REFERENCES config.member (id) ON DELETE CASCADE,
	review_document_id serial REFERENCES review.review_document (id) ON DELETE CASCADE,
	line_content_id serial REFERENCES git.line_content (id) ON DELETE CASCADE,
	CHECK (review_document_id = git.document_id_from_line_content(line_content_id)),

	description text NOT NULL,
	post_datetime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

	solved_flag boolean DEFAULT FALSE
);

CREATE TABLE review.discussion (
	id serial PRIMARY KEY,

	member_id serial REFERENCES config.member (id) ON DELETE CASCADE,
	issue_id serial REFERENCES review.issue (id) ON DELETE CASCADE,

	description text NOT NULL,
	post_datetime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--------------------------------------------------------------------------------

CREATE TABLE setting.verification_status (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

INSERT INTO setting.verification_status VALUES
	(1, 'unverified'),
	(2, 'pass'),
	(3, 'fail');

CREATE TABLE verification.verification_item (
	id serial PRIMARY KEY REFERENCES git.traceable_item (id) ON DELETE RESTRICT,

	review_document_id serial REFERENCES review.review_document (id) ON DELETE CASCADE,
	CHECK (review_document_id =  git.document_id_from_line_content(id)),

	status serial REFERENCES setting.verification_status (id) ON DELETE RESTRICT --DEFAULT 1
);