if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetPhonetypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetPhonetypes]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE [dbo].[GetPhonetypes]
	@lang_id int

 AS
/* select all phonetypes for a lang_id */

 SELECT  phonetype_id, typename 
 FROM phonetypes
 WHERE  lang_id = @lang_id
 ORDER BY phonetype_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

