CREATE PROCEDURE WebMasterSet 
@wmname VARCHAR(80), 
@wmaddress VARCHAR(80)  AS
/**
	DOCME: Document me!
**/

UPDATE sys_data SET value = @wmname WHERE type_id = 6
UPDATE sys_data SET value = @wmaddress WHERE type_id = 7


;
