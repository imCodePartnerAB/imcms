CREATE PROCEDURE [UpdateParentsDateModified] @meta_id INT AS
/**
	DOCME: Document me!
**/

UPDATE meta
SET date_modified = GETDATE() 
FROM meta
JOIN menus ON meta.meta_id = menus.meta_id
JOIN childs c ON c.menu_id = menus.menu_id
WHERE c.to_meta_id = @meta_id


;
