SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[delete_section]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[delete_section]
GO

CREATE PROCEDURE delete_section
 @section_id int
AS
 
 DELETE
 FROM meta_section
 WHERE section_id = @section_id
 DELETE 
 FROM sections
 WHERE section_id = @section_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

