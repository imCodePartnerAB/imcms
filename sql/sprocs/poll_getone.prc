CREATE PROCEDURE dbo.Poll_GetOne
	@meta_id int
	
AS
	SELECT *
	FROM polls 
	WHERE meta_id = @meta_id
