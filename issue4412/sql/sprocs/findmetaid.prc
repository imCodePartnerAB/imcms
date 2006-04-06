CREATE PROCEDURE FindMetaId
 @meta_id int
 AS
/**
	DOCME: Document me!
**/

SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id
