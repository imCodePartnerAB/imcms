SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getTemplategroups]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplategroups]
;


CREATE PROCEDURE getTemplategroups AS
/**
	DOCME: Document me!
**/

select group_id,group_name from templategroups order by group_name


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

