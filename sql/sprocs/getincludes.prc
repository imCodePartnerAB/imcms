SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetIncludes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetIncludes]
GO


CREATE PROCEDURE GetIncludes @meta_id INT AS
/**
	DOCME: Document me!
**/

SELECT include_id, included_meta_id  FROM includes WHERE meta_id = @meta_id
ORDER BY include_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

