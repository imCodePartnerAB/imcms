SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DeleteInclude]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteInclude]
GO


CREATE PROCEDURE DeleteInclude @meta_id INT, @include_id INT AS
/**
	DOCME: Document me!
**/

DELETE FROM includes WHERE meta_id = @meta_id AND include_id = @include_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

