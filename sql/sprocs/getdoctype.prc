SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetDocType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocType]
GO


CREATE PROCEDURE GetDocType
 @meta_id int
AS
/*
 Used by external systems to get the docType
*/
SELECT doc_type
FROM meta
WHERE meta_id = @meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

