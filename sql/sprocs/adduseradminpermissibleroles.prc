CREATE  PROCEDURE AddUseradminPermissibleRoles
/*
 Add role a Useradmin have administration rights on user with that roles
 A useradmin is only allowed to administrate users with those roles
*/
 @userId int,
 @roleId int
 
 AS
 INSERT INTO useradmin_role_crossref
 (user_id, role_id )
 VALUES (@userId, @roleId)
