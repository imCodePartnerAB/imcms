CREATE  PROCEDURE dbo.Poll_IncreaseAnswerOption
	@question_id int,
	@option_no int
	 
AS
	UPDATE poll_answers 
	SET answer_count = answer_count + 1
	WHERE question_id = @question_id and
		option_number = @option_no
