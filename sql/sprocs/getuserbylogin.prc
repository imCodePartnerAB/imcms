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
		user_id		INT		The id of the user
		login_name	CHAR(15)	The login-name. Essentially the same as the in-parameter @login.
		login_password	CHAR(15)	The users password.
		first_name	CHAR(25)	The users first name.
		last_name	CHAR(30)	The users last name.
		email		CHAR(50)	The users e-mail address.
		lang_prefix	CHAR(3)		The users language-prefix
		active		INT		Whether the user is allowed to log in.
**/

SELECT  user_id,
	login_name,
	login_password,
	first_name,
	last_name,
	email,
	lang_prefix,
	active
FROM users, lang_prefixes
WHERE login_name = @login
AND users.lang_id = lang_prefixes.lang_id


GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

