SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_IncreaseAnswerOption]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_IncreaseAnswerOption]
GO



/*
=============================================
Procedure Poll_IncreaseAnswerOption
Increase a poll answer option with 1 
=============================================
*/

CREATE  PROCEDURE dbo.Poll_IncreaseAnswerOption
	@question_id int,
	@option_no int
	 
AS
	UPDATE poll_answers 
	SET answer_count = answer_count + 1
	WHERE question_id = @question_id and
		option_number = @option_no

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

