SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetOne]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetOne]
;


/*
=============================================
Procedure Poll_GetOne
Get all data for a poll by meta_id
=============================================
*/


CREATE PROCEDURE dbo.Poll_GetOne
	@meta_id int
	
AS
	SELECT *
	FROM polls 
	WHERE meta_id = @meta_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

