SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserByLogin]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserByLogin]
GO



CREATE PROCEDURE GetUserByLogin @login varchar(50) AS
/**
	Get data for a user by his login_name. Used for login.
	Parameters:
		@login	The login_name of the user.

	Returns zero or one row with the following columns:
	
	user_id			INT			The id of the user
	login_name		VARCHAR(50)	The login-name. Essentially the same as the in-parameter @login.
	login_password	VARCHAR(15)	The users password.
	first_name		VARCHAR(25)	The users first name.
	last_name		VARCHAR(30)	The users last name.
	title			VARCHAR(30)
	company			VARCHAR(30)
	address			VARCHAR(40)
	city			VARCHAR(30)
	zip				VARCHAR(15)
	country			VARCHAR(30)
	county_council	VARCHAR(30)
	email			VARCHAR(50)	The users e-mail address.
	lang_id			int
	lang_prefix		char 3
	user_type 		int
	active			INT			Whether the user is allowed to log in.
	create_date		smalldatetime
	external                             int         Whether this user is handled solely within imcms or is synchronized externally
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
		create_date,
		[external]
		
FROM users, lang_prefixes
WHERE login_name = @login
AND users.lang_id = lang_prefixes.lang_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

