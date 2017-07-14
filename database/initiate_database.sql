CREATE SCHEMA setting;

CREATE TABLE setting.user_roll (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE
);

INSERT INTO setting.repository_user_roll VALUES
	(1, 'editor'),
	(2, 'reviewer'),
	(3, 'reader');

--------------------------------------------------------------------------------

CREATE SCHEMA config;

CREATE TABLE config.user (
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

CREATE TABLE config.organization_user_management_map (
	organization_id serial REFERENCES config.user (id) ON DELETE CASCADE,
	user_id serial REFERENCES config.user (id) ON DELETE RESTRICT,
	PRIMARY KEY (organization_id, user_id)
);

CREATE TABLE config.repository (
	id serial PRIMARY KEY,

	organization_id serial REFERENCES config.user (id) ON DELETE CASCADE,
	name text NOT NULL,
	display_name text,
	UNIQUE (organization_id, name),

	git_uri text NOT NULL
);

CREATE TABLE config.repository_user_map (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	user_id serial REFERENCES config.user (id) ON DELETE CASCADE,
	roll serial REFERENCES setting.repository_user_roll (id) ON DELETE RESTRICT,
	UNIQUE (repository_id, user_id)
);

--------------------------------------------------------------------------------

CREATE SCHEMA git;

CREATE TABLE git.commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES config.repository (id) ON DELETE CASCADE,
	sha text NOT NULL UNIQUE
);