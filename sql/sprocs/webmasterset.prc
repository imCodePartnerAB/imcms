SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[WebMasterSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[WebMasterSet]
GO


CREATE PROCEDURE WebMasterSet 
@wmname VARCHAR(80), 
@wmaddress VARCHAR(80)  AS
UPDATE sys_data SET value = @wmname WHERE type_id = 6
UPDATE sys_data SET value = @wmaddress WHERE type_id = 7


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

