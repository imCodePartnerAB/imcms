SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetQuestion]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetQuestion]
GO




/*
=============================================
Procedure Poll_GetQuestion
Get a question by meta_id and question number    
=============================================
*/


CREATE PROCEDURE dbo.Poll_GetQuestion
	@poll_id int,
	@question_no int
		
AS
	SELECT  *
	FROM 	poll_questions 
	WHERE poll_id = @poll_id and
		question_number = @question_no
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

