CREATE  PROCEDURE dbo.Poll_SetParameter
	@poll_id int, 
	@param_name varchar(25),
	@value varchar(500)
	
AS
	DECLARE @eval varchar(1000)
	SELECT @eval = ('
		UPDATE polls SET '+ @param_name + ' = '''+ @value + ''' WHERE id = '+ convert(varchar(10), @poll_id ) )

	EXEC (@eval)

;
