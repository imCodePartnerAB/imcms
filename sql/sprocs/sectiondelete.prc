SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionDelete]
;


CREATE PROCEDURE SectionDelete
 @section_id int
AS
 
 DELETE
 FROM meta_section
 WHERE section_id = @section_id
 DELETE 
 FROM sections
 WHERE section_id = @section_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

