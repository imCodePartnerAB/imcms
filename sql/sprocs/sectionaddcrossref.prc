SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionAddCrossref]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionAddCrossref]
;


CREATE PROCEDURE SectionAddCrossref 
 @meta_id int,
 @section_id int
AS
-- Lets insert the crossreferences but first we deleta all oldones for this meta_id
DELETE FROM meta_section
WHERE meta_id=@meta_id

IF (@section_id > 0)  BEGIN /* if we have a valid section */

	INSERT INTO meta_section (meta_id,section_id)
	VALUES (  @meta_id , @section_id )
END

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

