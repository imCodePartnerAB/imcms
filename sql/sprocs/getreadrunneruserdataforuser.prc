SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetReadrunnerUserDataForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetReadrunnerUserDataForUser]
GO


CREATE PROCEDURE GetReadrunnerUserDataForUser @user_id INT AS
/**
	Return readrunner-user-data for one user.
	Parameters:
		@user_id  The id of the user

	Returns one row with the following columns:
		uses                          INT      The number of times the user have used readrunner.
		max_uses                      INT      Maximum allowed amount of uses.
		max_uses_warning_threshold    INT      Percentage threshold at which the user will be warned about expiry.
		expiry_date                   DATETIME The last date the user may use readrunner.
		expiry_date_warning_threshold INT      Threshold of days before expiry_date at which the user will be warned about expiry.
        expiry_date_warning_sent      INT      Whether a expiry-date-warning has been sent or not.
**/
SELECT
	uses,
	max_uses,
	max_uses_warning_threshold,
	convert(varchar(10),expiry_date,120),
	expiry_date_warning_threshold,
	expiry_date_warning_sent
FROM
	readrunner_user_data
WHERE
	user_id = @user_id


GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

