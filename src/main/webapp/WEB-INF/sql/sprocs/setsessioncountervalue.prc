CREATE PROCEDURE SetSessionCounterValue
 @value int 
AS
 update sys_data
 set value = @value
 where type_id = 1
