SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindMetaId]
GO


CREATE PROCEDURE FindMetaId
 @meta_id int
 AS
/**
	DOCME: Document me!
**/

SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

