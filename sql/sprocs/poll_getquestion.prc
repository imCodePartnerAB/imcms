SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetQuestion]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetQuestion]
;




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
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

