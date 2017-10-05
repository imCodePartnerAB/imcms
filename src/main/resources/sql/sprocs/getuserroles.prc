CREATE PROCEDURE GetUserRoles
/*
Used to get all roles for a user
*/
 @aUserId int
 AS
 SELECT role_name 
 FROM roles,user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId
