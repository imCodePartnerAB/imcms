CREATE  PROCEDURE DeleteUseradminPermissibleRoles
/*
 Delete all roles from useradmin_roles_crossref for a user
*/
 @userId int
 AS
 DELETE FROM useradmin_role_crossref
 WHERE user_id = @userId
 


;
