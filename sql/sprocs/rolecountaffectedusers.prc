SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleCountAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCountAffectedUsers]
;


CREATE PROCEDURE RoleCountAffectedUsers
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts how many users who will be affected
*/
SELECT  DISTINCT COUNT(usr.role_id )
FROM user_roles_crossref usr, roles r, users u
WHERE usr.role_id = @aRoleId 
AND usr.user_id = u.user_id


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

