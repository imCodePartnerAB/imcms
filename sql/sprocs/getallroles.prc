SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetAllRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllRoles]
;


CREATE PROCEDURE GetAllRoles AS
/**
	DOCME: Document me!
**/

SELECT role_id, role_name
FROM roles
WHERE role_name not like 'Users'
 
ORDER BY role_name


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

