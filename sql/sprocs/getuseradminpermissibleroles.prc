if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUseradminPermissibleRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUseradminPermissibleRoles]
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

CREATE  PROCEDURE GetUseradminPermissibleRoles
/*
 get all roles a Useradmin have administration rights on user with that roles
*/
 @userId int
 AS
 
 	SELECT role_id, role_name
	FROM roles
	WHERE roles.role_id IN ( 
		SELECT role_id
 		FROM useradmin_role_crossref  
 		WHERE user_id = @userId )


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

