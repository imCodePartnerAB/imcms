if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUseradminPermissibleRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUseradminPermissibleRoles]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

CREATE  PROCEDURE GetUseradminPermissibleRoles
/*
 get all roles a Useradmin have administration rights on user with that roles
*/
 @userId int
 AS
 SELECT role_id
 FROM useradmin_role_crossref 
 WHERE user_id = @userId


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

