CREATE PROCEDURE RemoveUserFromRole
 @userId int, @role_id int
AS
/* removes user from role */
DELETE 
FROM user_roles_crossref
WHERE user_id = @userId and role_id = @role_id
