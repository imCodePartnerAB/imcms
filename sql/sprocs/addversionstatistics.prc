SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddVersionStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddVersionStatistics]
GO


CREATE PROCEDURE AddVersionStatistics @name VARCHAR(30), @version VARCHAR(30) AS
DECLARE @string VARCHAR(62)
SET @string = @name+': '+@version
EXEC AddStatistics @string


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

