SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionChangeAndDeleteCrossref]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionChangeAndDeleteCrossref]
GO


CREATE PROCEDURE SectionChangeAndDeleteCrossref
  @new_section_id int,
  @old_section_id  int
AS
update meta_section
set section_id = @new_section_id
where section_id=@old_section_id

exec SectionDelete @old_section_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

