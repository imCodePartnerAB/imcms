SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_sections_count]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_sections_count]
GO

CREATE PROCEDURE get_sections_count 
 @section_id int
AS
/*
Gets the number of docs that is connected to that section_id
*/
select count(meta_id) 
from meta_section
where section_id=@section_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

