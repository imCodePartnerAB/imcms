SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSetEx]
GO


CREATE PROCEDURE GetUserPermissionSetEx @meta_id INT, @user_id INT AS
/*
 Finds out what extended permissions (extra permissiondata) the user has for this document.
 Does not return correct data for a superadmin, or full admin, so check that first.
*/
SELECT dps.permission_id, dps.permission_data
FROM   roles_rights rr
JOIN   user_roles_crossref urc
      ON urc.user_id = @user_id
      AND rr.role_id = urc.role_id
JOIN  meta m
      ON m.meta_id = @meta_id
      AND rr.meta_id = m.meta_id
JOIN  doc_permission_sets_ex dps
      ON dps.meta_id = m.meta_id
      AND rr.set_id = dps.set_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

