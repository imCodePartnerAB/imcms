if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DeleteUseradminPermissibleRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteUseradminPermissibleRoles]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

CREATE  PROCEDURE DeleteUseradminPermissibleRoles
/*
 Delete all roles from useradmin_roles_crossref for a user
*/
 @userId int
 AS
 DELETE FROM useradmin_role_crossref
 WHERE user_id = @userId
 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

