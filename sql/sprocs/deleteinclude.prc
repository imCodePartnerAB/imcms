CREATE PROCEDURE DeleteInclude @meta_id INT, @include_id INT AS
/**
	DOCME: Document me!
**/

DELETE FROM includes WHERE meta_id = @meta_id AND include_id = @include_id


;
