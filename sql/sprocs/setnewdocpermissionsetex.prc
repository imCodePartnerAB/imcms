SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetNewDocPermissionSetEx]
GO


CREATE PROCEDURE SetNewDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS
/*
 Updates an extended permissionset for a document.
*/
-- Insert new value
INSERT INTO new_doc_permission_sets_ex
VALUES (@meta_id,@set_id,@permission_id, @permission_data)


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

