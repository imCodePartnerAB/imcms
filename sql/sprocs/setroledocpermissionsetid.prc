SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetRoleDocPermissionSetId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetRoleDocPermissionSetId]
;


CREATE PROCEDURE SetRoleDocPermissionSetId @role_id INT, @meta_id INT, @set_id INT AS
-- First delete the previous set_id
DELETE FROM   roles_rights 
WHERE   meta_id = @meta_id
  AND  role_id = @role_id
-- Now insert the new one
IF @set_id < 4
BEGIN
 INSERT INTO roles_rights (role_id, meta_id, set_id)
 VALUES ( @role_id, @meta_id, @set_id )
END


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

