SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetAllUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllUsersInList]
GO


CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

