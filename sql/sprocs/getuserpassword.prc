CREATE PROCEDURE GetUserPassword 
/* Used by AdminUserProps servlet to retrieve the users password 
*/
 @user_id int
AS
DECLARE @retVal char(15)
SELECT @retVal  = login_password 
FROM USERS
WHERE user_id = @user_id
SELECT @retVal =  ISNULL(@retVal , '') 
SELECT @retVal AS 'Password'


;
