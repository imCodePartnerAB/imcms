SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[change_and_delete_section_crossrefs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[change_and_delete_section_crossrefs]
GO

CREATE PROCEDURE change_and_delete_section_crossrefs
  @new_section_id int,
  @old_section_id  int
AS
update meta_section
set section_id = @new_section_id
where section_id=@old_section_id

exec delete_section @old_section_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

