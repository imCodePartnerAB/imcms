CREATE  PROCEDURE GetUserPhones
 @user_id int
AS
/*
Used to generate a list with all type of users. Used from UserChangePrefs
*/
SELECT p.phone_id, RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id


;
