if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_GetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_GetAll]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO


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
	

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

