CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS
/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active
WHERE user_id = @user_id
