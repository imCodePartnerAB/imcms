CREATE PROCEDURE SystemMessageSet
 @newMsg varchar(1000)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3
