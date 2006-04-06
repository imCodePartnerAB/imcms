CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS
/**
	DOCME: Document me!
**/

SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type
