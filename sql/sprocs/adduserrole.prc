CREATE PROCEDURE AddUserRole
/*
  Adds a role to a particular user
*/
 @user_id int,
 @role_id int
AS
INSERT INTO  user_roles_crossref(user_id, role_id) VALUES( @user_id , @role_id ) 
