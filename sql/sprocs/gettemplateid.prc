SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTemplateId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateId]
;


CREATE PROCEDURE GetTemplateId
 @aTemplatename varchar(80)
 AS
/**
	DOCME: Document me!
**/

SELECT template_id
FROM templates
WHERE simple_name = @aTemplatename


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

