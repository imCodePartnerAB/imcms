SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[UpdateParentsDateModified]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateParentsDateModified]
GO


CREATE PROCEDURE [UpdateParentsDateModified] @meta_id INT AS
UPDATE meta
SET date_modified = GETDATE() 
FROM meta JOIN childs c
ON meta.meta_id = c.meta_id 
WHERE c.to_meta_id = @meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

