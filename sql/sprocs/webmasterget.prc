SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[WebMasterGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[WebMasterGet]
GO


CREATE PROCEDURE WebMasterGet AS
DECLARE @wmname VARCHAR(80)
DECLARE @wmaddress VARCHAR(80)
SELECT @wmname = value FROM sys_data WHERE type_id = 6
SELECT @wmaddress = value FROM sys_data WHERE type_id = 7
SELECT @wmname,@wmaddress


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

