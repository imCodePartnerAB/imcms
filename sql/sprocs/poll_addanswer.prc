SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_AddAnswer]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_AddAnswer]
;



/*
=============================================
Procedure Poll_AddAnswer
Add a new poll answer
=============================================
*/


CREATE  PROCEDURE Poll_AddAnswer
	@question_id int,
	@text_id int,
	@option_no int
	
	
AS
	INSERT INTO poll_answers ( question_id, text_id, option_number )
	VALUES ( @question_id, @text_id, @option_no )

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

