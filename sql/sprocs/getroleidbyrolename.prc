CREATE PROCEDURE GetRoleIdByRoleName
	@roleName varchar(30)

 AS

SELECT role_id 
FROM roles
WHERE role_name like @roleName 
;
