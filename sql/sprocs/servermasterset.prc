SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ServerMasterSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ServerMasterSet]
GO


CREATE PROCEDURE ServerMasterSet 
@smname VARCHAR(80), 
@smaddress VARCHAR(80)  AS
/**
	DOCME: Document me!
**/

UPDATE sys_data SET value = @smname WHERE type_id = 4
UPDATE sys_data SET value = @smaddress WHERE type_id = 5


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

