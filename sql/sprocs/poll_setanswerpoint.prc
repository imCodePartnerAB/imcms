SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_SetAnswerPoint]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_SetAnswerPoint]
;



/*
=============================================
Procedure Poll_SetAnswerPoint
Set the point value to an existing answer option
=============================================
*/


CREATE  PROCEDURE dbo.Poll_SetAnswerPoint
	@answer_id int,
	@point int

	
AS
	UPDATE poll_answers
	SET option_point = @point
	WHERE [id] = @answer_id


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

