CREATE PROCEDURE Poll_AddQuestion
	@poll_id int, 
	@question_no int,
	@text_id int 
AS
	INSERT INTO poll_questions 
	VALUES (@poll_id, @question_no, @text_id)
