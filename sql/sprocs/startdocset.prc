CREATE PROCEDURE StartDocSet @meta_id INT AS
/**
	Changes the start document
**/

UPDATE sys_data SET value = @meta_id WHERE type_id = 0
