SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS OFF
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SortOrder_GetExistingDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SortOrder_GetExistingDocs]
GO


CREATE PROCEDURE SortOrder_GetExistingDocs
	@langPrefixString char(3)
AS
/*
This sproc is used by the GetExistingDoc servlet, it takes the lang id string as argument and returns
the sortorder options  display text for that language.
*/
SELECT sType.sort_by_type , display.display_name
FROM lang_prefixes lang
INNER JOIN display_name display
	ON display.lang_id = lang.lang_id
	AND lang.lang_prefix = @langPrefixString
INNER JOIN sort_by sType
	ON sType.sort_by_id = display.sort_by_id


GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

