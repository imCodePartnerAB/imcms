SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[RoleFindName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleFindName]
GO


CREATE PROCEDURE RoleFindName
 @newRoleName varchar(25)
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnVal int
SELECT  @returnVal = r.role_id
FROM roles r
WHERE r.role_name = @newRoleName
-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , -1 )
SELECT @returnVal AS 'FoundRoleName'


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

