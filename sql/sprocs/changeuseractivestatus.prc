SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ChangeUserActiveStatus]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ChangeUserActiveStatus]
GO


CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS
/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active
WHERE user_id = @user_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

