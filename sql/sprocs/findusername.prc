SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FindUserName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindUserName]
GO


CREATE PROCEDURE FindUserName
 @userName varchar(50)
AS
/*
 This function is used from the conference when  someone is logging in to the 
conference. The system searches for the username and returns the 
userId, userName and password
*/
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

