SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesDocPermissions]
GO


CREATE PROCEDURE GetUserRolesDocPermissions @meta_id INT, @user_id INT AS
SELECT
  r.role_id,
  r.role_name,
  ISNULL(rr.set_id,4),
  ISNULL(urc.role_id,0)
FROM
  roles_rights rr 
RIGHT JOIN 
  roles r 
      ON  rr.role_id = r.role_id
      AND rr.meta_id = @meta_id
LEFT JOIN user_roles_crossref urc
      ON r.role_id = urc.role_id
      AND urc.user_id = @user_id
WHERE r.role_id > 0
ORDER BY role_name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

