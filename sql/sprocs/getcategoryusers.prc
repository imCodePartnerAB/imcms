SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetCategoryUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCategoryUsers]
GO


CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
 @category int
AS
SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
FROM users
WHERE user_type = @category
ORDER BY last_name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

