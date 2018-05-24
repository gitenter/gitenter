CREATE SCHEMA settings;

CREATE TABLE settings.member (
	id serial PRIMARY KEY,
	username text NOT NULL UNIQUE,
	password text NOT NULL,
	display_name text NOT NULL,
	email text NOT NULL CHECK (email ~* '(^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$)|(^$)'),
	registration_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	last_active_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE settings.ssh_key (
	id serial PRIMARY KEY,
	member_id serial REFERENCES settings.member (id) ON DELETE CASCADE,

	/* 
	 * Key type has limited possibilities of “ecdsa-sha2-nistp256”, 
	 * “ecdsa-sha2-nistp384”, “ecdsa-sha2-nistp521”, “ssh-ed25519”, 
	 * “ssh-dss” or “ssh-rsa”.
	 *
	 * However, it is handled by the I/O and security package,
	 * so we don't need to link a reference table in here.
	 *
	 * An `authorized_keys` file may also have a set of options
	 * (Refer to: http://man.openbsd.org/sshd.8#AUTHORIZED_KEYS_FILE_FORMAT)
	 * but that is not user defined, so should not be set in here.
	 */
	key_type text NOT NULL, 
	key_data bytea NOT NULL, /* Should be unique. But I loose the constrain a little bit at this moment. */
	comment text
);

CREATE TABLE settings.organization (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE,
	display_name text NOT NULL
);

CREATE TABLE settings.organization_manager_map (
	member_id serial REFERENCES settings.member (id) ON DELETE RESTRICT,
	organization_id serial REFERENCES settings.organization (id) ON DELETE CASCADE,
	PRIMARY KEY (member_id, organization_id)
);

CREATE TABLE settings.repository (
	id serial PRIMARY KEY,

	organization_id serial REFERENCES settings.organization (id) ON DELETE CASCADE,
	name text NOT NULL,
	display_name text NOT NULL,
	description text,
	UNIQUE (organization_id, name),

	is_public boolean NOT NULL
);

CREATE TABLE settings.repository_member_map (
	id serial PRIMARY KEY,

	repository_id serial REFERENCES settings.repository (id) ON DELETE CASCADE,
	member_id serial REFERENCES settings.member (id) ON DELETE CASCADE,
	/*
	 * With this constrain, a user can at most have one role.
	 */
	UNIQUE (repository_id, member_id),

	/*
	 * Rather than a lookup table in SQL, we define the enum types
	 * in the persistent layer. A "shortName" is used, as (1) the 
	 * ordinal of an Enum depends on the ordering of its values 
	 * and can create problems, if we need to add new ones, and
	 * (2) the String representation of an Enum is often quite 
	 * verbose and renaming a value will break the database mapping.
	 *
	 * Currently we have "role" to have values
	 * R for READER
	 * V for REVIEWER
	 * E for EDITOR
	 * L for PROJECT_LEADER
	 */
	role char(1) NOT NULL CHECK (role='R' OR role='V' OR role='E' OR role='L')
);

--------------------------------------------------------------------------------

CREATE SCHEMA git;

CREATE TABLE git.git_commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES settings.repository (id) ON DELETE CASCADE,
	sha_checksum_hash text NOT NULL,

	/*
	 * It is not save to make "sha_checksum_hash" itself unique,
	 * since empty repositories make at the same time (should be up 
	 * to second?) will have exactly the same "sha_checksum_hash".
	 * It happens in my automatic UI test case, and causes SQL errors.
	 *
	 * On the other hand, although not exactly true, it is quite safe
	 * to make (repository_id, sha_checksum_hash) pair unique. Since
	 * Different commits belongs to the same repository should at least
	 * modified a little bit, there's no problem.
	 * (Automatic code with add something commit and delete something and
	 * commit again may have problem, but that's not a natural case).
	 */
	UNIQUE(repository_id, sha_checksum_hash)
);

CREATE TABLE git.git_commit_valid (
	/*
	 * There is a constrain that the "id" of table "git_commit_valid",
	 * "git_commit_invalid", and "git_commit_ignored" are mutually exclusive,
	 * but there seems no easy way to define it in PostgreSQL.
	 */
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
	commit_id serial REFERENCES git.git_commit_valid (id) ON DELETE CASCADE,
	relative_filepath text NOT NULL,
	UNIQUE(commit_id, relative_filepath)
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

CREATE TABLE git.traceable_item (
	id serial PRIMARY KEY,

	document_id serial REFERENCES git.document (id) ON DELETE CASCADE,
	item_tag text NOT NULL,
	content text NOT NULL,
	UNIQUE (document_id, item_tag)	
);

CREATE FUNCTION git.document_id_from_traceable_item (integer) 
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT tra.document_id INTO return_id FROM git.traceable_item AS tra
	WHERE tra.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE UNIQUE INDEX traceable_item_tag_unique_per_commit_idx
	ON git.traceable_item (
		git.commit_id_from_document(id), 
		item_tag
);

CREATE TABLE git.traceability_map (
	upstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	downstream_item_id serial REFERENCES git.traceable_item (id) ON DELETE CASCADE,
	PRIMARY KEY (upstream_item_id, downstream_item_id),

	CHECK (git.commit_id_from_document(git.document_id_from_traceable_item(upstream_item_id)) 
		= git.commit_id_from_document(git.document_id_from_traceable_item(downstream_item_id)))
);

--------------------------------------------------------------------------------

CREATE SCHEMA review;

--------------------------------------------------------------------------------

CREATE SCHEMA verification;