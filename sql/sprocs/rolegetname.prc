CREATE PROCEDURE RoleGetName
 @roleId int
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnStr varchar(25)
SELECT  @returnStr = r.role_name
FROM roles r
WHERE r.role_id = @roleId
-- Lets validate for null
SELECT @returnStr = ISNULL(  @returnStr , '---' )
SELECT @returnStr AS 'Rolename'
