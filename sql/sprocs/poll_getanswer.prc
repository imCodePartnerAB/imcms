SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetAnswer]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetAnswer]
GO



/*
=============================================
Procedure Poll_GetAnswer
Get one answer option for a question    
=============================================
*/


CREATE  PROCEDURE dbo.Poll_GetAnswer
	@question_id int,
	@option_no int 
AS
	SELECT  *
	FROM poll_answers 
	WHERE question_id = @question_id and
	      option_number = @option_no



GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

