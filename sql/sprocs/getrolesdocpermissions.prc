SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRolesDocPermissions]
GO


CREATE PROCEDURE GetRolesDocPermissions @meta_id INT AS
/* Selects all roles except for superadmin, and returns the permissionset each has for the document. */
SELECT
  r.role_id,
  r.role_name,
  ISNULL(rr.set_id,4)
FROM
  roles_rights rr 
RIGHT JOIN 
  roles r 
      ON  rr.role_id = r.role_id
      AND rr.meta_id = @meta_id
WHERE r.role_id > 0
ORDER BY role_name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

