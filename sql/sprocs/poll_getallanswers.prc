CREATE PROCEDURE Poll_GetAllAnswers
	@question_id int
		
AS
	SELECT  *
	FROM poll_answers 
	WHERE question_id = @question_id 
	ORDER BY option_number
;
