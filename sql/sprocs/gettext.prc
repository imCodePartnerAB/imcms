CREATE PROCEDURE GetText @meta_id INT, @no INT AS
/*
	Retrieve a text with type
*/
SELECT  text, type FROM texts WHERE meta_id = @meta_id AND name = @no
