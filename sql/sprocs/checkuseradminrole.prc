SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

/****** Object:  Stored Procedure dbo.checkUserAdminrole    Script Date: 11/27/2002 09:37:33 ******/
if exists (select * from sysobjects where id = object_id('dbo.checkUserAdminrole') and sysstat & 0xf = 4)
	drop procedure dbo.checkUserAdminrole
GO



CREATE      PROCEDURE  dbo.checkUserAdminrole 
	@userId int,
	@adminRole int
 AS


SELECT     roles.admin_role
FROM         user_roles_crossref INNER JOIN
                      roles ON user_roles_crossref.role_id = roles.role_id
WHERE     (user_roles_crossref.user_id = @userId) AND (roles.admin_role = @adminRole)





GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

