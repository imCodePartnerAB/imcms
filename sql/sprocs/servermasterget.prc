SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ServerMasterGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ServerMasterGet]
GO


CREATE PROCEDURE ServerMasterGet AS
/**
	DOCME: Document me!
**/

DECLARE @smname VARCHAR(80)
DECLARE @smaddress VARCHAR(80)
SELECT @smname = value FROM sys_data WHERE type_id = 4
SELECT @smaddress = value FROM sys_data WHERE type_id = 5
SELECT @smname,@smaddress


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

