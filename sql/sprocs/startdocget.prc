CREATE PROCEDURE StartDocGet AS
/**
	Returns the start document
**/

SELECT value FROM sys_data WHERE sys_id = 0

GO
