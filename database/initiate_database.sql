CREATE SCHEMA setting;

--------------------------------------------------------------------------------

CREATE SCHEMA config;

CREATE TABLE config.member (
	id serial PRIMARY KEY,
	username text NOT NULL UNIQUE,
	password text NOT NULL,
	display_name text,
	email text NOT NULL CHECK (email ~* '(^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$)|(^$)')
);

CREATE TABLE config.organization (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE,
	display_name text
);

CREATE TABLE config.organization_manager_map (
	member_id serial REFERENCES config.member (id) ON DELETE RESTRICT,
	organization_id serial REFERENCES config.organization (id) ON DELETE CASCADE,
	PRIMARY KEY (member_id, organization_id)
);

CREATE TABLE config.repository (
	id serial PRIMARY KEY,

	organization_id serial REFERENCES config.organization (id) ON DELETE CASCADE,
	name text NOT NULL,
	display_name text,
	UNIQUE (organization_id, name),

	git_uri text NOT NULL UNIQUE
);

CREATE TABLE setting.repository_member_roll (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

INSERT INTO setting.repository_member_roll VALUES
	(1, 'reader'),
	(2, 'reviewer'),
	(3, 'editor'),
	(4, 'project lead');

CREATE TABLE config.repository_member_map (
	id serial PRIMARY KEY,

	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	member_id serial REFERENCES config.member (id) ON DELETE CASCADE,
	UNIQUE (repository_id, member_id),

	roll serial REFERENCES setting.repository_member_roll (id) ON DELETE RESTRICT --DEFAULT 3
);

--------------------------------------------------------------------------------

CREATE SCHEMA git;

CREATE TABLE git.git_commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	sha_checksum_hash text NOT NULL UNIQUE
);

CREATE TABLE git.document (
	id serial PRIMARY KEY,
	commit_id serial REFERENCES git.git_commit (id) ON DELETE CASCADE
);

CREATE TABLE git.document_modified (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE CASCADE,
	relative_filepath text NOT NULL
);

CREATE TABLE git.document_unmodified (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE CASCADE,
	original_document_id serial REFERENCES git.document (id) ON DELETE RESTRICT
);

CREATE FUNCTION git.commit_id_from_document (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT txf.commit_id INTO return_id FROM git.document AS txf
	WHERE txf.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.line_content (
	id serial PRIMARY KEY,

	document_id serial REFERENCES git.document_modified (id) ON DELETE CASCADE,
	line_number integer NOT NULL,
	UNIQUE (document_id, line_number)	
);

CREATE FUNCTION git.document_id_from_line_content (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT lct.document_id INTO return_id FROM git.line_content AS lct
	WHERE lct.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE FUNCTION git.commit_id_from_line_content (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT lct.document_id INTO return_id FROM 
	(
		git.document AS txf
		JOIN
		git.line_content AS lct
		ON
		txf.id = lct.document_id
	)
	WHERE lct.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.traceable_item (
	id serial PRIMARY KEY REFERENCES git.line_content (id) ON DELETE RESTRICT,
	item_tag text NOT NULL
);

CREATE UNIQUE INDEX ON git.traceable_item (git.commit_id_from_line_content(id), item_tag);

CREATE TABLE git.traceability_map (
	id serial PRIMARY KEY,
	commit_id serial REFERENCES git.git_commit (id) ON DELETE CASCADE,
	upstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	downstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	UNIQUE (upstream_item_id, downstream_item_id),

	CHECK (git.commit_id_from_line_content(upstream_item_id) = git.commit_id_from_line_content(downstream_item_id))
);

CREATE INDEX ON git.traceability_map (upstream_item_id);
CREATE INDEX ON git.traceability_map (downstream_item_id);

--------------------------------------------------------------------------------

CREATE SCHEMA review;

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

CREATE SCHEMA verification;

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