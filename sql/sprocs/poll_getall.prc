if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetAll]
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;


/*
=============================================
Procedure Poll_GetAll
Get all polls
=============================================
*/


CREATE PROCEDURE Poll_GetAll
		
AS
	SELECT *
	FROM polls 
	

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

