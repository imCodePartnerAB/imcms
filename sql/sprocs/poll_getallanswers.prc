SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetAllAnswers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetAllAnswers]
GO



/*
=============================================
Procedure Poll_GetAllAnswers
Get all answer for one question    
=============================================
*/


CREATE PROCEDURE Poll_GetAllAnswers
	@question_id int
		
AS
	SELECT  *
	FROM poll_answers 
	WHERE question_id = @question_id 
	ORDER BY option_number
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

