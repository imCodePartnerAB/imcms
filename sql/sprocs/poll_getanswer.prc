CREATE  PROCEDURE dbo.Poll_GetAnswer
	@question_id int,
	@option_no int 
AS
	SELECT  *
	FROM poll_answers 
	WHERE question_id = @question_id and
	      option_number = @option_no



;
