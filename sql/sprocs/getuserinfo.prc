SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserInfo]
GO


CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/
 @aUserId int
AS
 SELECT user_id,
	login_name,
	login_password,
	first_name,
	last_name,
	title,	        -- Unused?
	company,        -- Unused?
	address,        -- Unused?
	city,           -- Unused?
	zip,            -- Unused?
	country,        -- Unused?
	county_council, -- Unused?
	email,
	external,
	last_page,      -- Unused?
	archive_mode,   -- Unused?
	users.lang_id,
	active,
	create_date,    -- Unused?
        lang_prefix

 FROM users, lang_prefixes
 WHERE user_id = @aUserId
 AND  users.lang_id = lang_prefixes.lang_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

