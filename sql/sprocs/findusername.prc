CREATE PROCEDURE FindUserName
 @userName varchar(50)
AS
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName
