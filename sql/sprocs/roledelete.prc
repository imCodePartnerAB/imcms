SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDelete]
;


CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
 @roleId int
AS
DELETE FROM ROLES_RIGHTS WHERE role_id = @roleId
DELETE FROM user_roles_crossref WHERE role_id =@roleId
DELETE FROM ROLES WHERE role_id = @roleId


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

