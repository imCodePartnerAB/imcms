SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RolePermissionsAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RolePermissionsAddNew]
GO


CREATE PROCEDURE RolePermissionsAddNew
 @newRoleName varchar(25), @permissions int
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r
INSERT INTO roles (  role_id , role_name, permissions )
VALUES( @newRoleId , @newRoleName, @permissions )


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

