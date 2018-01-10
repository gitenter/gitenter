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

CREATE TABLE git.git_commit_valid (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE
);

CREATE TABLE git.git_commit_invalid (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE,
	error_message text NOT NULL
);

CREATE TABLE git.git_commit_ignored (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE
);

CREATE TABLE git.document (
	id serial PRIMARY KEY,
	commit_id serial REFERENCES git.git_commit_valid (id) ON DELETE CASCADE
);

CREATE FUNCTION git.commit_id_from_document (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT doc.commit_id INTO return_id FROM git.document AS doc
	WHERE doc.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.document_modified (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE CASCADE,
	relative_filepath text NOT NULL
);

-- Since there is a PL/pgSQL function involved, there is no way
-- to give a unique constrain simply by
-- UNIQUE (git.commit_id_from_document(id), relative_filepath)
-- So we need to create this index.
CREATE UNIQUE INDEX document_modified_unique_relative_filepath_idx 
	ON git.document_modified (
		git.commit_id_from_document(id), 
		relative_filepath
	);

CREATE FUNCTION git.relative_filepath_from_document_modified (integer) 
RETURNS text AS $return$
DECLARE return text;
BEGIN
	SELECT mod.relative_filepath INTO return FROM git.document_modified AS mod
	WHERE mod.id = $1;
	RETURN return;
END;
$return$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE git.document_unmodified (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE CASCADE,
	original_document_id serial REFERENCES git.document_modified (id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX document_unmodified_unique_relative_filepath_idx 
	ON git.document_unmodified (
		git.commit_id_from_document(id), 
		git.relative_filepath_from_document_modified(original_document_id)
	);

-- document_modified.id and document_unmodified.id are mutually exclusive,
-- but there seems no easy way to define it in PostgreSQL.

-- Actually there should be a stronger constrain that consider
-- both modified and unmodified documents, commit_id with relative_filepath
-- is unique. But there seems no easy way to define it in PostgreSQL.

CREATE TABLE git.traceable_item (
	id serial PRIMARY KEY,

	original_document_id serial REFERENCES git.document_modified (id) ON DELETE CASCADE,
	item_tag text NOT NULL,
	content text NOT NULL,
	UNIQUE (original_document_id, item_tag)	
);

CREATE TABLE git.traceability_map (
	id serial PRIMARY KEY,
	upstream_document_id serial REFERENCES git.document (id) ON DELETE CASCADE,
	upstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	downstream_document_id serial REFERENCES git.document (id) ON DELETE CASCADE,
	downstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	UNIQUE (upstream_item_id, downstream_item_id),

	CHECK (git.commit_id_from_document(upstream_document_id) = git.commit_id_from_document(downstream_document_id))
);

CREATE INDEX ON git.traceability_map (upstream_document_id, upstream_item_id);
CREATE INDEX ON git.traceability_map (downstream_document_id, downstream_item_id);

--------------------------------------------------------------------------------

CREATE SCHEMA review;

--------------------------------------------------------------------------------

CREATE SCHEMA verification;