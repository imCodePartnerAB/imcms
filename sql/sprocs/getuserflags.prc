SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserFlags]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserFlags]
GO


CREATE PROCEDURE GetUserFlags AS
/**
    Get a list of all user flags
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

