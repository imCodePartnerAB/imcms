if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetRoleIdByRoleName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRoleIdByRoleName]
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE [dbo].[GetRoleIdByRoleName]
	@roleName varchar(30)

 AS

SELECT role_id 
FROM roles
WHERE role_name like @roleName 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

