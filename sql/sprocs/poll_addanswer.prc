CREATE  PROCEDURE Poll_AddAnswer
	@question_id int,
	@text_id int,
	@option_no int
	
	
AS
	INSERT INTO poll_answers ( question_id, text_id, option_number )
	VALUES ( @question_id, @text_id, @option_no )
