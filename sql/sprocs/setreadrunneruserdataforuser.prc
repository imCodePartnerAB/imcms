SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetReadrunnerUserDataForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetReadrunnerUserDataForUser]
GO


CREATE PROCEDURE SetReadrunnerUserDataForUser
	@user_id                       INT,
	@uses                          INT,
	@max_uses                      INT,
	@max_uses_warning_threshold    INT,
	@expiry_date                   DATETIME,
	@expiry_date_warning_threshold INT
AS
/**
	Set readrunner-user-data for one user.
	Parameters:
		@user_id  The id of the user
		@uses                          INT      The number of times the user have used readrunner.
		@max_uses                      INT      Maximum allowed amount of uses.
		@max_uses_warning_threshold    INT      Percentage threshold at which the user will be warned about expiry.
		@expiry_date                   DATETIME The last date the user may use readrunner.
		@expiry_date_warning_threshold INT      Threshold of days before expire_date at which the user will be warned about expiry.
**/
DELETE FROM readrunner_user_data WHERE user_id = @user_id

INSERT INTO readrunner_user_data
VALUES(
	@user_id,
	@uses,
	@max_uses,
	@max_uses_warning_threshold,
	@expiry_date,
	@expiry_date_warning_threshold
)


GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

