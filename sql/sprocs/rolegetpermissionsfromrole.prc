SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleGetPermissionsFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetPermissionsFromRole]
GO


CREATE PROCEDURE RoleGetPermissionsFromRole @role_id int, @lang_prefix varchar(3) AS
/*
  select rolepermission from role id
*/
SELECT  ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
FROM   roles_permissions rp
LEFT JOIN  roles r
     ON rp.permission_id & r.permissions != 0
     AND r.role_id = @role_id
WHERE lang_prefix = @lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

