SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddUserRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddUserRole]
GO


CREATE PROCEDURE AddUserRole
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
-- Lets check if the role already exists
DECLARE @foundFlag int
SET @foundFlag = 0 
SELECT @foundFlag = ref.role_id
FROM user_roles_crossref ref
WHERE ref.role_id = @aRole_id
 AND ref.user_id = @aUser_id
IF @@rowcount  = 0 BEGIN
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
END
/*
CREATE PROCEDURE AddUserRole
 Adds a role to a particular user
 @aUser_id int,
 @aRole_id int
AS
/**
	DOCME: Document me!
**/

 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
*/ 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

