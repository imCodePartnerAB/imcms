CREATE PROCEDURE DelUserRoles
  @userToChangeId int,
  @roleId int
AS
/**
	Delete roles for a user
	If roleId = -1 then the administrator is a Superadmin and we have to delete
	all roles.
	Else the administrator is a Useradmin and we delete only a one role  
**/
IF @roleId = -1 BEGIN
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @userToChangeId
END

ELSE IF @roleId > 0 BEGIN
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @userToChangeId AND role_id = @roleId	
END
