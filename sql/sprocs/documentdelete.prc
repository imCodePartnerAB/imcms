CREATE PROCEDURE DocumentDelete
	@meta_id int
AS
/*
Deletes a meta Id in the system. Used by func deleteDocAll in the ImcService class
*/

BEGIN TRAN

delete from document_categories where meta_id = @meta_id
delete from meta_classification where meta_id = @meta_id
delete from childs where to_meta_id = @meta_id
delete from childs where menu_id in (select menu_id from menus where meta_id = @meta_id)
delete from menus where meta_id = @meta_id
delete from text_docs where meta_id = @meta_id
delete from texts where meta_id = @meta_id  
delete from images where meta_id = @meta_id  
delete from roles_rights where meta_id = @meta_id  
delete from user_rights where meta_id = @meta_id  
delete from url_docs where meta_id = @meta_id 
delete from browser_docs where meta_id = @meta_id 
delete from fileupload_docs where meta_id = @meta_id  
delete from frameset_docs where meta_id = @meta_id
delete from new_doc_permission_sets_ex where meta_id = @meta_id
delete from new_doc_permission_sets where meta_id = @meta_id
delete from doc_permission_sets_ex where meta_id = @meta_id
delete from doc_permission_sets where meta_id = @meta_id
delete from includes where meta_id = @meta_id
delete from includes where included_meta_id = @meta_id
delete from meta_section where meta_id = @meta_id
delete from meta where meta_id = @meta_id

IF @@error = 0
BEGIN
	COMMIT TRAN
END
ELSE
BEGIN
	ROLLBACK TRAN
END

;
