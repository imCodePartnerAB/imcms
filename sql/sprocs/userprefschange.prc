SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[UserPrefsChange]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UserPrefsChange]
GO


CREATE PROCEDURE UserPrefsChange
  @aUserId int
/*
  Returns the information for a user which he is able to change self. Observer that we
  return the password as an empty string
*/
AS
-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email, lang_id --, profession, company
FROM users
WHERE user_id = @aUserId 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

