SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ServerMasterSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ServerMasterSet]
;


CREATE PROCEDURE ServerMasterSet 
@smname VARCHAR(80), 
@smaddress VARCHAR(80)  AS
/**
	DOCME: Document me!
**/

UPDATE sys_data SET value = @smname WHERE type_id = 4
UPDATE sys_data SET value = @smaddress WHERE type_id = 5


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

