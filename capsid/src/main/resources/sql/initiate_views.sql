CREATE VIEW git.current_document AS
	SELECT 
		git.document.id AS id,
		git.document.commit_id AS commit_id,
		git.modified_document.relative_filepath AS relative_filepath
	FROM
	(
		git.document
		JOIN
		git.modified_document
		ON
		git.document.id = git.modified_document.id
	);

--SELECT * FROM git.current_document;
