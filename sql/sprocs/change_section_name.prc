SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[change_section_name]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[change_section_name]
GO

CREATE PROCEDURE change_section_name
 @section_id int,
 @new_name varchar(200)
AS
 UPDATE sections
 set section_name= @new_name
 WHERE section_id = @section_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

