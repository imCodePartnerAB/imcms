

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddUseradminPermissibleRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddUseradminPermissibleRoles]
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

CREATE  PROCEDURE AddUseradminPermissibleRoles
/*
 Add role a Useradmin have administration rights on user with that roles
 A useradmin is only allowed to administrate users with those roles
*/
 @userId int,
 @roleId int
 
 AS
 INSERT INTO useradmin_role_crossref
 (user_id, role_id )
 VALUES (@userId, @roleId)
 

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

