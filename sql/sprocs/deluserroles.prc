SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DelUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelUserRoles]
GO


CREATE PROCEDURE DelUserRoles
 @aUserId int
AS
/**
	DOCME: Document me!
**/

 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @aUserId


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

