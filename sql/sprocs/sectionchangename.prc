SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SectionChangeName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SectionChangeName]
GO


CREATE PROCEDURE SectionChangeName
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

