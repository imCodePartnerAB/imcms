CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users


;
