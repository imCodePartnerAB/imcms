SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DocumentDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DocumentDelete]
GO


CREATE PROCEDURE [dbo].[DocumentDelete] 
	@meta_id int
AS
/*
Deletes a meta Id in the system. Used by func deleteDocAll in the ImcService class
*/
delete from meta_classification where meta_id = @meta_id
delete from childs where to_meta_id = 	@meta_id   
delete from childs where meta_id =	@meta_id 
delete from text_docs where meta_id = 	@meta_id  
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
delete from meta where meta_id = @meta_id
delete from meta where meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

