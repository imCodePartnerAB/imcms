CREATE PROCEDURE dbo.Poll_GetQuestion
	@poll_id int,
	@question_no int
		
AS
	SELECT  *
	FROM 	poll_questions 
	WHERE poll_id = @poll_id and
		question_number = @question_no
