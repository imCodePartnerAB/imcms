CREATE  PROCEDURE dbo.Poll_SetAnswerPoint
	@answer_id int,
	@point int

	
AS
	UPDATE poll_answers
	SET option_point = @point
	WHERE [id] = @answer_id


;
