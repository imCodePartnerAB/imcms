CREATE  PROCEDURE GetUseradminPermissibleRoles
/*
 get all roles a Useradmin have administration rights on user with that roles
*/
 @userId int
 AS
 
 	SELECT role_id, role_name
	FROM roles
	WHERE roles.role_id IN ( 
		SELECT role_id
 		FROM useradmin_role_crossref  
 		WHERE user_id = @userId )


;
