SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleCheckConferenceAllowed]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCheckConferenceAllowed]
GO


CREATE PROCEDURE RoleCheckConferenceAllowed 
 @lang_prefix varchar(3),
 @lookForRoleId int 
AS
-- Checks if the role passed, is still avaible to use for the conference when 
-- a user tries to add himself in the conflogin servlet
DECLARE @bitNbrMaxValue int
SELECT @bitNbrMaxValue = 2  -- Max value the bit position we look for
SELECT  r.role_id, r.role_name
FROM   roles_permissions rp
JOIN  roles r
 ON rp.permission_id & r.permissions & @bitNbrMaxValue  != 0
 AND r.role_id = @lookForRoleId
WHERE lang_prefix = @lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

