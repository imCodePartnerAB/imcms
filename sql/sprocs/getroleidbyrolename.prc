if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetRoleIdByRoleName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRoleIdByRoleName]
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE GetRoleIdByRoleName
	@roleName varchar(30)

 AS

SELECT role_id 
FROM roles
WHERE role_name like @roleName 
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

