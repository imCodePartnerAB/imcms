CREATE PROCEDURE Poll_GetAllQuestions
	@poll_id int
		
AS
	SELECT  *
	FROM poll_questions 
	WHERE poll_id = @poll_id 
	ORDER BY question_number
