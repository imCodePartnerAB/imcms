CREATE PROCEDURE RoleUpdateName
/*
Updates the name on a role in the db
*/
 @role_id int,
 @newRole_name varchar(25)
AS
UPDATE ROLES
SET role_name = @newRole_name
WHERE role_id = @role_id 


;
