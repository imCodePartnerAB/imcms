SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_all_sections_count]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_all_sections_count]
GO

CREATE PROCEDURE get_all_sections_count AS
/*
Gets all the section_id and  section_name and the number of docs
*/
select s.section_id, s.section_name, count(meta_id) 
from sections s
left join meta_section ms on s.section_id = ms.section_id
group by s.section_name, s.section_id
order by section_name
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

