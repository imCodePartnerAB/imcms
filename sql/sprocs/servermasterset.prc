CREATE PROCEDURE ServerMasterSet 
@smname VARCHAR(80), 
@smaddress VARCHAR(80)  AS
/**
	DOCME: Document me!
**/

UPDATE sys_data SET value = @smname WHERE type_id = 4
UPDATE sys_data SET value = @smaddress WHERE type_id = 5
