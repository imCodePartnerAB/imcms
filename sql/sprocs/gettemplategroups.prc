SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getTemplategroups]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplategroups]
GO


CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

