CREATE PROCEDURE GetHighestUserId
AS
SELECT MAX(user_id) +1
FROM users
