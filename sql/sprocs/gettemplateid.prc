SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTemplateId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateId]
GO


CREATE PROCEDURE GetTemplateId
 @aTemplatename varchar(80)
 AS
SELECT template_id
FROM templates
WHERE simple_name = @aTemplatename


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

