SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_AddQuestion]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_AddQuestion]
GO


/*
=============================================
Procedure Poll_AddQuestion
Insert a new poll question
=============================================
*/


CREATE PROCEDURE dbo.Poll_AddQuestion
	@poll_id int, 
	@question_no int,
	@text_id int 
AS
	INSERT INTO poll_questions 
	VALUES (@poll_id, @question_no, @text_id)
	

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

