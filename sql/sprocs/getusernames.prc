CREATE PROCEDURE GetUserNames
/* 
This procedure is used to retrieve a users full name (first name + last name
concateneted.
*/
 @user_id int,
 @what int
AS
 DECLARE @returnVal char(25)
IF(@what = 1) BEGIN
 SELECT @returnVal = RTRIM(first_name) 
 FROM users
 WHERE users.user_id = @user_id 
END ELSE BEGIN  
 SELECT @returnVal =  RTRIM(last_name) 
 FROM users
 WHERE users.user_id = @user_id 
END
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'UserName'


;
