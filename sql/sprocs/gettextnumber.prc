SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTextNumber]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTextNumber]
GO


CREATE PROCEDURE GetTextNumber @meta_id int,  @name int AS
/* selects text name @number from meta id @meta_id */
select text from 
 texts 
where
 meta_id = @meta_id  and name = @name


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

