SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddScreenStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddScreenStatistics]
GO


CREATE PROCEDURE AddScreenStatistics @width INT, @height INT, @bits INT AS
DECLARE @screen VARCHAR(20) 
SET @screen = 'Screen: '+LTRIM(STR(@width))+'x'+LTRIM(STR(@height))+'x'+LTRIM(STR(@bits))
EXEC AddStatistics @screen


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

