SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserFlagsForUserOfType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserFlagsForUserOfType]
GO

CREATE PROCEDURE GetUserFlagsForUserOfType @user_id INT, @type INT AS

/**
    Get a list of the flags for a single user of a single type
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags,
		user_flags_crossref
WHERE	user_flags.user_flag_id = user_flags_crossref.user_flag_id
AND		user_flags_crossref.user_id = @user_id
AND		user_flags.type = @type
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

