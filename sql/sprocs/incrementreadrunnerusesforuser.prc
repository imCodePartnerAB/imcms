SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[IncrementReadrunnerUsesForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IncrementReadrunnerUsesForUser]
GO


CREATE PROCEDURE IncrementReadrunnerUsesForUser @user_id INT AS
/**
	Increment the uses of readrunner for one user.
	Parameters:
		@user_id  The id of the user
**/
UPDATE readrunner_user_data
SET uses = uses + 1
WHERE user_id = @user_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

