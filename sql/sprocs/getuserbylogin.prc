SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserByLogin]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserByLogin]
GO


CREATE PROCEDURE GetUserByLogin @login varchar(15) AS
/**
	Get data for a user by his login_name. Used for login.
	Parameters:
		@login	The login_name of the user.

	Returns zero or one row with the following columns:
	
		user_id			INT		The id of the user
		login_name		CHAR(15)	The login-name. Essentially the same as the in-parameter @login.
		login_password	CHAR(15)	The users password.
		first_name		CHAR(25)	The users first name.
		last_name		CHAR(30)	The users last name.
		title			char 30
		company			char 30
		address			char 40
		city			char 30
		zip				char 15
		country			char 30
		county_council	char 30
		email			CHAR(50)	The users e-mail address.
		lang_id			int
		lang_prefix		char 3
		user_type 		int
		active			INT			Whether the user is allowed to log in.
		create_date		smalldatetime
**/

SELECT  user_id,
		login_name,
		login_password,
		first_name,
		last_name,
		title,
		company,
		address,
		city,
		zip,
		country,
		county_council,
		email,
		users.lang_id,
		lang_prefix,
		user_type,
		active,
		create_date
		
FROM users, lang_prefixes
WHERE login_name = @login
AND users.lang_id = lang_prefixes.lang_id


GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

