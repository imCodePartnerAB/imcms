SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddBrowserStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddBrowserStatistics]
GO


CREATE PROCEDURE AddBrowserStatistics @os VARCHAR(30), @browser varchar(30), @version varchar(30) AS
DECLARE @newline CHAR(2)
SET @newline = CHAR(13)+CHAR(10)
DECLARE @browserstring VARCHAR(120)
SET @browserstring =  'Os: '+@os+@newline+
   'Browser: '+@browser+@newline+
   'Version: '+@version
EXEC AddStatistics @browserstring


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

