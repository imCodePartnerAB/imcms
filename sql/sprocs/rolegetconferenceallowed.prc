SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleGetConferenceAllowed]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetConferenceAllowed]
GO


CREATE PROCEDURE RoleGetConferenceAllowed
 @lang_prefix varchar(3)
AS
/*
  select all roles for a certain language which has the the bitmaskflag
  set for the selfregister in conference permission.
  As the permissionId = 2, then it is the bitposition nbr 2 we have
 to look at. The maximun value for bitpos 2 is 2, so 
  Eftersom permissionid är 2 --> så är det bit nr 2 vi är ute efter.
  Maxvärdet för bit nr 2 är 2, bit nr 1 = 1, bit nr 3 = 4, bit nr 4  
*/
--SELECT  ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
DECLARE @bitNbrMaxValue int
SELECT @bitNbrMaxValue = 2  -- Max value the bit position we look for
SELECT  r.role_id, r.role_name
FROM   roles_permissions rp
JOIN  roles r
 ON rp.permission_id & r.permissions & @bitNbrMaxValue  != 0
 --AND r.role_id = 5
WHERE lang_prefix = @lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

