SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleUpdateName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleUpdateName]
GO


CREATE PROCEDURE RoleUpdateName
/*
Updates the name on a role in the db
*/
 @role_id int,
 @newRole_name varchar(25)
AS
UPDATE ROLES
SET role_name = @newRole_name
WHERE role_id = @role_id 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

