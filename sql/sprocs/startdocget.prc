SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

/****** Object:  Stored Procedure StartDocGet    Script Date: 2002-09-25 14:08:52 ******/
if exists (select * from sysobjects where id = object_id('dbo.StartDocGet') and sysstat & 0xf = 4)
	drop procedure dbo.StartDocGet
GO

CREATE PROCEDURE StartDocGet AS
/**
	Returns the start document
**/

SELECT value FROM sys_data WHERE sys_id = 0


GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

