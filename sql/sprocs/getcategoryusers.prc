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
 @category int,
 @searchString varchar(20)
AS

IF @category = -1 BEGIN  

	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' )
	ORDER BY last_name
END

ELSE BEGIN
	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE user_type = @category
	AND ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' )
	ORDER BY last_name
END

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

