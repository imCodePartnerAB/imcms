SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypes]
GO


CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS
/**
	DOCME: Document me!
**/

SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

