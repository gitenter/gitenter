CREATE VIEW git.current_document AS
	SELECT 
		git.document.id AS id,
		git.document.commit_id AS commit_id,
		git.document_modified.relative_filepath AS relative_filepath
	FROM
	(
		git.document
		JOIN
		git.document_modified
		ON
		git.document.id = git.document_modified.id
	);

--SELECT * FROM git.current_document;
