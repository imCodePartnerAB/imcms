SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionCount]
;


CREATE PROCEDURE SectionCount 
 @section_id int
AS
/*
Gets the number of docs that is connected to that section_id
*/
select count(meta_id) 
from meta_section
where section_id=@section_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

