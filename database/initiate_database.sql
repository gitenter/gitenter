CREATE SCHEMA setting;

--------------------------------------------------------------------------------

CREATE SCHEMA config;

CREATE TABLE config.person (
	id serial PRIMARY KEY,
	username text NOT NULL UNIQUE,
	password text NOT NULL,
	display_name text,
	email text CHECK (email ~* '(^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$)|(^$)')
);

CREATE TABLE config.organization (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE,
	display_name text NOT NULL
);

CREATE TABLE config.organization_manager_map (
	organization_id serial REFERENCES config.organization (id) ON DELETE CASCADE,
	person_id serial REFERENCES config.person (id) ON DELETE RESTRICT,
	PRIMARY KEY (organization_id, person_id)
);

CREATE TABLE config.repository (
	id serial PRIMARY KEY,

	organization_id serial REFERENCES config.organization (id) ON DELETE CASCADE,
	name text NOT NULL,
	display_name text NOT NULL,
	UNIQUE (organization_id, name),

	git_uri text NOT NULL
);

CREATE TABLE setting.repository_person_roll (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

INSERT INTO setting.repository_person_roll VALUES
	(1, 'editor'),
	(2, 'reviewer'),
	(3, 'reader');

CREATE TABLE config.repository_person_map (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	person_id serial REFERENCES config.person (id) ON DELETE CASCADE,
	roll serial REFERENCES setting.repository_person_roll (id) ON DELETE RESTRICT,
	UNIQUE (repository_id, person_id)
);

--------------------------------------------------------------------------------

CREATE SCHEMA git;

CREATE TABLE git.git_commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	sha_checksum_hash text NOT NULL UNIQUE
);

CREATE TABLE git.text_file (
	id serial PRIMARY KEY,
	commit_id serial REFERENCES git.git_commit (id) ON DELETE CASCADE,
	filepath text NOT NULL UNIQUE
);

CREATE FUNCTION git.commit_id_from_text_file (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT txf.commit_id INTO return_id FROM git.text_file AS txf
	WHERE txf.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.line_content (
	id serial PRIMARY KEY,

	text_file_id serial REFERENCES git.text_file (id) ON DELETE CASCADE,
	line_number integer NOT NULL,
	UNIQUE (text_file_id, line_number)	
);

CREATE FUNCTION git.commit_id_from_line_content (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT lct.text_file_id INTO return_id FROM 
	(
		git.text_file AS txf
		JOIN
		git.line_content AS lct
		ON
		txf.id = lct.text_file_id
	)
	WHERE lct.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.traceable_item (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE RESTRICT,
	item_tag text NOT NULL
);

CREATE UNIQUE INDEX ON git.traceable_item (git.commit_id_from_line_content(id), item_tag);

CREATE TABLE git.traceability_map (
	upstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	downstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	PRIMARY KEY (upstream_item_id, downstream_item_id),

	CHECK (git.commit_id_from_line_content(upstream_item_id) = git.commit_id_from_line_content(downstream_item_id))
);

CREATE INDEX ON git.traceability_map (upstream_item_id);
CREATE INDEX ON git.traceability_map (downstream_item_id);

--------------------------------------------------------------------------------

CREATE SCHEMA review;

CREATE TABLE review.milestone (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE RESTRICT,
	description text NOT NULL 
);

CREATE TABLE review.comment (
	id serial PRIMARY KEY,

	person_id serial REFERENCES config.person (id) ON DELETE CASCADE,
);