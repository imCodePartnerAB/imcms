SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetLanguageList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLanguageList]
GO


CREATE PROCEDURE GetLanguageList
 @user_lang_prefix VARCHAR(3)
AS
/*
 Returns all 
*/
SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
AND lang.user_prefix = @user_lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

