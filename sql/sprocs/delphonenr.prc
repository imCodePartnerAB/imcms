SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DelPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelPhoneNr]
GO


CREATE PROCEDURE DelPhoneNr
 @aUserId int
AS
 DELETE 
 FROM phones
 WHERE user_id = @aUserId


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

