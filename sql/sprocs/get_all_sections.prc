SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_all_sections]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_all_sections]
GO

CREATE PROCEDURE get_all_sections AS
/*
Gets all the section_id and  section_name
*/
SELECT section_id, section_name
 FROM sections
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

