if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetAllQuestions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetAllQuestions]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

/*
=============================================
Procedure Poll_GetAllQuestions
Get all questions for one poll    
=============================================
*/


CREATE PROCEDURE Poll_GetAllQuestions
	@poll_id int
		
AS
	SELECT  *
	FROM poll_questions 
	WHERE poll_id = @poll_id 
	ORDER BY question_number

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

