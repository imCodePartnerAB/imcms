CREATE PROCEDURE GetAllRoles AS
/**
	DOCME: Document me!
**/

SELECT role_id, role_name
FROM roles
WHERE role_name not like 'Users'
 
ORDER BY role_name
