SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypes]
GO


CREATE PROCEDURE ListDocsGetInternalDocTypes @lang_prefix VARCHAR(3) AS
/* selct all internal doc types */
select doc_type, type 
from doc_types
where doc_type <= 100
and lang_prefix = @lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

