SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSet]
GO


CREATE PROCEDURE GetUserPermissionSet @meta_id INT, @user_id INT AS
/*
 Finds out what is the most privileged permission_set a user has for a document.
 Column 1: The users most privileged set_id
 Column 2: The users permission-set for this set_id
 Column 3: The permissions for this document. ( At the time of this writing, the only permission there is is wether or not set_id 1 is more privileged than set_id 2, and it's stored in bit 0 )
 set_id's:
 0 - most privileged (full rights)
 1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
 3 - only read rights
 4 - least privileged (no rights)
*/
SELECT TOP 1 ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4),
  ISNULL(dps.permission_id,0),
  ISNULL(m.permissions,0)
FROM   roles_rights rr
FULL JOIN  user_roles_crossref urc
      ON urc.user_id = @user_id
      AND rr.meta_id = @meta_id
      AND (
        rr.role_id = urc.role_id
       OR urc.role_id < 1
       )      
RIGHT JOIN  meta m
      ON m.meta_id = @meta_id
      AND urc.user_id = @user_id
      AND (
        rr.meta_id IS NOT NULL
       OR urc.role_id = 0
       )
LEFT JOIN doc_permission_sets dps
      ON dps.meta_id = @meta_id
      AND rr.set_id = dps.set_id
GROUP BY ISNULL(dps.permission_id,0),m.permissions
ORDER BY ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4)


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

