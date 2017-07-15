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
	display_name text
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
	display_name text,
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

CREATE TABLE git.commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	sha_checksum_hash text NOT NULL UNIQUE
);