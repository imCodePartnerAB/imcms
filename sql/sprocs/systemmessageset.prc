CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
 @newMsg varchar(80)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3


;
