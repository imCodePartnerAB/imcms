CREATE PROCEDURE SetNewDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS
/*
 Updates a permissionset for a document.
*/
-- Delete the previous value
DELETE FROM new_doc_permission_sets
WHERE meta_id = @meta_id
AND  set_id = @set_id
-- Insert new value
INSERT INTO new_doc_permission_sets
VALUES (@meta_id,@set_id,@permission_id)


;
