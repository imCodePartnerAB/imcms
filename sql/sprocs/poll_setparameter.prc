SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_SetParameter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_SetParameter]
GO



/*
=============================================
Procedure Poll_SetParameter
Update a poll parameter
=============================================
*/


CREATE  PROCEDURE dbo.Poll_SetParameter
	@poll_id int, 
	@param_name varchar(25),
	@value varchar(500)
	
AS
	DECLARE @eval varchar(1000)
	SELECT @eval = ('
		UPDATE polls SET '+ @param_name + ' = '''+ @value + ''' WHERE id = '+ convert(varchar(10), @poll_id ) )

	EXEC (@eval)

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

