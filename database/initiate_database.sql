CREATE SCHEMA auth;

CREATE TABLE auth.member (
	id serial PRIMARY KEY,
	username text NOT NULL UNIQUE,
	password text NOT NULL,
	display_name text NOT NULL,
	email text NOT NULL CHECK (email ~* '(^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$)|(^$)'),
	registration_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth.organization (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE,
	display_name text NOT NULL
);

CREATE TABLE auth.organization_member_map (
	id serial PRIMARY KEY,

	/*
	 * With this constrain, a member can at most have one role
	 * in a particular organization.
	 */
	organization_id serial REFERENCES auth.organization (id) ON DELETE CASCADE,
	member_id serial REFERENCES auth.member (id) ON DELETE RESTRICT,
	UNIQUE (organization_id, member_id),

	/*
	 * Rather than a lookup table in SQL, we define the enum types
	 * in the persistent layer. A "shortName" is used, as (1) the
	 * ordinal of an Enum depends on the ordering of its values
	 * and can create problems, if we need to add new ones, and
	 * (2) the String representation of an Enum is often quite
	 * verbose and renaming a value will break the database mapping.
	 *
	 * The detailed business logic is in the application level.
	 *
	 * Currently we have "role_shortname" to have values:
	 * G: non-professional manager
	 * M: ordinary member
	 */
	role_shortname char(1) NOT NULL CHECK (role_shortname='G' OR role_shortname='M')
);

CREATE TABLE auth.repository (
	id serial PRIMARY KEY,

	organization_id serial REFERENCES auth.organization (id) ON DELETE CASCADE,
	name text NOT NULL,
	display_name text NOT NULL,
	description text,
	UNIQUE (organization_id, name),

	is_public boolean NOT NULL
);

CREATE TABLE auth.repository_member_map (
	id serial PRIMARY KEY,

	repository_id serial REFERENCES auth.repository (id) ON DELETE CASCADE,
	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,
	/*
	 * With this constrain, a user can at most have one role on
	 * some particular repository.
	 */
	UNIQUE (repository_id, member_id),

	/*
	 * Currently we have "role_shortname" to have values:
	 * O: project organizer
	 * E: document editor
	 * R: document reviewer
	 * B: blacklist
	 */
	role_shortname char(1) NOT NULL CHECK (role_shortname='O' OR role_shortname='E' OR role_shortname='R' OR role_shortname='B')
);

CREATE TABLE auth.ssh_key (
	id serial PRIMARY KEY,
	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,

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
	key_data bytea NOT NULL UNIQUE, /* Should be unique. But I loose the constrain a little bit at this moment. */
	comment text
);

CREATE TABLE auth.member_feature_toggle (
	id serial PRIMARY KEY,
	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,

	feature_shortname char(1) NOT NULL,
	UNIQUE(member_id, feature_shortname),

	is_on boolean NOT NULL
);

CREATE TABLE auth.repository_feature_toggle (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES auth.repository (id) ON DELETE CASCADE,

	feature_shortname char(1) NOT NULL,
	UNIQUE(repository_id, feature_shortname),

	is_on boolean NOT NULL
);

--------------------------------------------------------------------------------

CREATE SCHEMA git;

CREATE TABLE git.git_commit (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES auth.repository (id) ON DELETE CASCADE,
	sha text NOT NULL,

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
	UNIQUE(repository_id, sha)
);

CREATE TABLE git.valid_commit (
	/*
	 * There is a constrain that the "id" of table "valid_commit",
	 * "invalid_commit", and "ignored_commit" are mutually exclusive,
	 * but there seems no easy way to define it in PostgreSQL.
	 */
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE
);

CREATE TABLE git.invalid_commit (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE,
	error_message text NOT NULL
);

CREATE TABLE git.ignored_commit (
	id serial PRIMARY KEY REFERENCES git.git_commit (id) ON DELETE CASCADE
);

CREATE TABLE git.document (
	id serial PRIMARY KEY,
	commit_id serial REFERENCES git.valid_commit (id) ON DELETE CASCADE,
	relative_path text NOT NULL,
	UNIQUE(commit_id, relative_path)
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
