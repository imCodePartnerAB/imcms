CREATE PROCEDURE Poll_AddNew
	@meta_id int
	
AS
	INSERT INTO polls ( meta_id )
	VALUES ( @meta_id )


;
