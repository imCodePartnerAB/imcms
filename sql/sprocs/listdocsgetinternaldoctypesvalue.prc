SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypesValue]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypesValue]
GO


CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS
/* selct all internal doc types */
select distinct doc_type
from doc_types
where doc_type <= 100


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

