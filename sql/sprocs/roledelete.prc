CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
 @roleId int
AS
DELETE FROM ROLES_RIGHTS WHERE role_id = @roleId
DELETE FROM user_roles_crossref WHERE role_id =@roleId
DELETE FROM ROLES WHERE role_id = @roleId


;
