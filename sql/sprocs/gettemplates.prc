SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplates]
GO


CREATE PROCEDURE getTemplates AS
/**
	DOCME: Document me!
**/

select template_id, simple_name from templates


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

