SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DeleteNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteNewDocPermissionSetEx]
GO


CREATE PROCEDURE DeleteNewDocPermissionSetEx @meta_id INT, @set_id INT AS
/*
 Delete extended permissions for a permissionset for a document
*/
DELETE FROM  new_doc_permission_sets_ex
WHERE  meta_id = @meta_id
  AND set_id = @set_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

