SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Poll_AddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Poll_AddNew]
GO

/*
=============================================
Procedure Poll_AddNew
Add a new poll
=============================================
*/


CREATE PROCEDURE Poll_AddNew
	@meta_id int
	
AS
	INSERT INTO polls ( meta_id )
	VALUES ( @meta_id )


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

