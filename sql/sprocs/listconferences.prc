SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ListConferences]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListConferences]
GO


CREATE PROCEDURE ListConferences AS
/**
	DOCME: Document me!
**/

select meta_id, meta_headline 
from meta 
where doc_type = 102


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

