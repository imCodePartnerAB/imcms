SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserFlagsForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserFlagsForUser]
;


CREATE PROCEDURE GetUserFlagsForUser @user_id INT AS
/**
    Get a list of the flags for a single user
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags,
		user_flags_crossref
WHERE		user_flags.user_flag_id = user_flags_crossref.user_flag_id
AND		user_flags_crossref.user_id = @user_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

