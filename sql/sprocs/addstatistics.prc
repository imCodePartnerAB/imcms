SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddStatistics]
GO


CREATE PROCEDURE AddStatistics @name VARCHAR(120) AS
UPDATE stats
SET  num = num + 1
WHERE name = @name
IF @@ROWCOUNT = 0
BEGIN
INSERT stats
VALUES ( @name,
   1
  )
END


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

