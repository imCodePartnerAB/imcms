SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTextDocData]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTextDocData]
GO


CREATE PROCEDURE GetTextDocData @meta_id INT AS
SELECT t.template_id, simple_name, sort_order, t.group_id
FROM   text_docs t  
JOIN   templates c 
     ON t.template_id = c.template_id
WHERE meta_id = @meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

