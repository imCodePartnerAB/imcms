if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DeleteUseradminPermissibleRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteUseradminPermissibleRoles]
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

CREATE  PROCEDURE DeleteUseradminPermissibleRoles
/*
 Delete all roles from useradmin_roles_crossref for a user
*/
 @userId int
 AS
 DELETE FROM useradmin_role_crossref
 WHERE user_id = @userId
 


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

