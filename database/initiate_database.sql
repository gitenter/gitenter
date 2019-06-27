CREATE SCHEMA auth;

CREATE TABLE auth.member (
	id serial PRIMARY KEY,
	username text NOT NULL UNIQUE,
	password text NOT NULL,
	display_name text NOT NULL,
	email text NOT NULL CHECK (email ~* '(^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$)|(^$)'),
	register_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth.organization (
	id serial PRIMARY KEY,
	name text NOT NULL UNIQUE,
	display_name text NOT NULL
);

CREATE TABLE auth.organization_member_map (
	id serial PRIMARY KEY,

	/*
	 * "ON DELETE RESTRICT" for member because otherwise when a member
	 * is removed, some organization will become orphan ones with no
	 * manager (and nobody can further add manager/member). This force
	 * delete organization duties before a member can be removed from
	 * the system.
	 */
	organization_id serial REFERENCES auth.organization (id) ON DELETE CASCADE,
	member_id serial REFERENCES auth.member (id) ON DELETE RESTRICT,

	/*
	 * With this constrain, a member can at most have one role
	 * in a particular organization.
	 */
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
	 * B: blacklist
	 *
	 * "Reviewer" is a property per-review, rather than per-repository,
	 * and it is not a persistent property (a user who used to review a
 	 * repository may later on loose read-access of the same repository).
	 * Therefore, there's no role of "reviewer" in here.
	 *
	 * "Editor" is a property right now, which has nothing to do with
	 * the "author" of a review. Anybody made change for the a particular
	 * review will be an "author".
	 */
	role_shortname char(1) NOT NULL CHECK (role_shortname='O' OR role_shortname='E' OR role_shortname='B')
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
	key_data text NOT NULL UNIQUE, /* Should be unique. But I loose the constrain a little bit at this moment. */
	comment text
);

CREATE TABLE auth.member_feature_toggle (
	id serial PRIMARY KEY,
	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,

	feature_shortname char(1) NOT NULL,
	UNIQUE(member_id, feature_shortname),

	is_on boolean NOT NULL
);

-- TODO:
-- Add a lock on whether the git files are allowed to be modified (as obviously)
-- you don't want multiple people/server instance to `git push` which trigger the
-- modification of the git files of one repo at the same time.
-- Or maybe we should use Redis to implement this lock.
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
	 * It is not save to make `sha` itself unique, since empty repositories
	 * make at the same time (should be up to second?) will have exactly
	 * the same "sha_checksum_hash". It happens in my automatic UI test case,
	 * and causes SQL errors.
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

CREATE INDEX repository_id_in_git_commit ON git.git_commit (repository_id);

CREATE FUNCTION git.repository_id_from_commit (integer)
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT cmt.repository_id INTO return_id FROM git.git_commit AS cmt
	WHERE cmt.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

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
	content text,
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

	/*
	 * Here is a double-JOIN. If becomes so slow, may create another
	 * method to do JOIN inside, or even remove this constrain.
	 */
	CHECK (git.commit_id_from_document(git.document_id_from_traceable_item(upstream_item_id))
		= git.commit_id_from_document(git.document_id_from_traceable_item(downstream_item_id)))
);

--------------------------------------------------------------------------------

CREATE SCHEMA review;

CREATE TABLE review.review (
	id serial PRIMARY KEY,
	repository_id serial REFERENCES auth.repository (id) ON DELETE CASCADE,

	version_number text NOT NULL,
	description text,
	UNIQUE (repository_id, version_number)
);

CREATE FUNCTION review.repository_id_from_review (integer)
RETURNS integer AS $return_id$
DECLARE return_id integer;
BEGIN
	SELECT rev.repository_id INTO return_id FROM review.review AS rev
	WHERE rev.id = $1;
	RETURN return_id;
END;
$return_id$ LANGUAGE plpgsql
IMMUTABLE;

CREATE TABLE review.attendee (
	id serial PRIMARY KEY,
	review_id serial REFERENCES review.review (id) ON DELETE CASCADE,
	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,
	UNIQUE (review_id, member_id)
);

-- CREATE TABLE review.author (
-- 	id serial PRIMARY KEY REFERENCES review.attendee (id) ON DELETE CASCADE
-- );

CREATE TABLE review.reviewer (
	id serial PRIMARY KEY REFERENCES review.attendee (id) ON DELETE CASCADE,
	liability_description text
);

CREATE TABLE review.subsection (
	id serial PRIMARY KEY REFERENCES git.valid_commit (id) ON DELETE CASCADE,
	review_id serial REFERENCES review.review (id) ON DELETE CASCADE,
	member_id serial REFERENCES auth.member (id) ON DELETE RESTRICT,
	CHECK (git.repository_id_from_commit(id) = review.repository_id_from_review(review_id)),

	create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review.discussion_subsection (
	id serial PRIMARY KEY REFERENCES review.subsection (id) ON DELETE CASCADE,

	deadline timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review.finalization_subsection (
	id serial PRIMARY KEY REFERENCES review.subsection (id) ON DELETE CASCADE
);

CREATE TABLE review.in_review_document (
	id serial PRIMARY KEY REFERENCES git.document (id) ON DELETE CASCADE,
	subsection_id serial REFERENCES review.subsection (id) ON DELETE CASCADE,
	CHECK (git.commit_id_from_document(id) = subsection_id),

	previous_version_id integer REFERENCES review.in_review_document (id) ON DELETE CASCADE,

	/*
	 * A: approved
	 * P: approved with postscripts
	 * R: request changes
	 * D: denied
	 */
	status_shortname char(1) NOT NULL CHECK (status_shortname='A' OR status_shortname='P' OR status_shortname='R' OR status_shortname='D'),
	status_setup_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review.vote (
	id serial PRIMARY KEY,
	document_id serial REFERENCES review.in_review_document (id) ON DELETE CASCADE,
	reviewer_id serial REFERENCES review.reviewer (id) ON DELETE CASCADE,
	UNIQUE (document_id, reviewer_id),

	status_shortname char(1) NOT NULL CHECK (status_shortname='A' OR status_shortname='P' OR status_shortname='R' OR status_shortname='D')
);

CREATE TABLE review.discussion_topic (
	id serial PRIMARY KEY,
	document_id serial REFERENCES review.in_review_document (id) ON DELETE CASCADE,
	line_number integer NOT NULL
);

-- CREATE TABLE review.author_map (
-- 	author_id serial REFERENCES review.author (id) ON DELETE CASCADE,
-- 	document_id serial REFERENCES review.in_review_document (id) ON DELETE CASCADE,
-- 	PRIMARY KEY (author_id, document_id)
-- );

CREATE TABLE review.review_meeting (
	id serial PRIMARY KEY,
	subsection_id serial REFERENCES review.discussion_subsection (id) ON DELETE CASCADE,
	start_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review.review_meeting_attendee_map (
	attendee_id serial REFERENCES review.attendee (id) ON DELETE CASCADE,
	review_meeting_id serial REFERENCES review.review_meeting (id) ON DELETE CASCADE,
	PRIMARY KEY (attendee_id, review_meeting_id)
);

CREATE TABLE review.review_meeting_record (
	id serial PRIMARY KEY REFERENCES review.discussion_topic (id) ON DELETE CASCADE,
	review_meeting_id serial REFERENCES review.review_meeting (id) ON DELETE CASCADE,

	content text,
	record_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review.online_discussion_topic (
	id serial PRIMARY KEY REFERENCES review.discussion_topic (id) ON DELETE CASCADE
);

CREATE TABLE review.comment (
	id serial PRIMARY KEY,
	discussion_topic_id serial REFERENCES review.online_discussion_topic (id) ON DELETE CASCADE,
	attendee_id serial REFERENCES review.attendee (id) ON DELETE CASCADE,

	content text,
	comment_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/*
 * TODO:
 * A general notification system for all kinds of events?
 */
-- CREATE TABLE review.notification (
-- 	id serial PRIMARY KEY,
-- 	member_id serial REFERENCES auth.member (id) ON DELETE CASCADE,
-- 	discussion_topic_id serial REFERENCES review.discussion_topic (id) ON DELETE CASCADE,
--
-- 	is_read boolean NOT NULL DEFAULT FALSE,
-- 	notify_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

--------------------------------------------------------------------------------

CREATE SCHEMA verification;
