SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionChangeAndDeleteCrossref]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionChangeAndDeleteCrossref]
;


CREATE PROCEDURE SectionChangeAndDeleteCrossref
  @new_section_id int,
  @old_section_id  int
AS
update meta_section
set section_id = @new_section_id
where section_id=@old_section_id

exec SectionDelete @old_section_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

