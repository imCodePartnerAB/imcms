SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

/****** Object:  Stored Procedure dbo.GetPhonetypeName    Script Date: 2003-01-17 14:11:03 ******/
if exists (select * from sysobjects where id = object_id('dbo.GetPhonetypeName') and sysstat & 0xf = 4)
	drop procedure dbo.GetPhonetypeName
GO



CREATE  PROCEDURE GetPhonetypeName
	@phonetype_id int,
	@lang_id int
 AS

select typename from phonetypes
where phonetype_id = @phonetype_id and lang_id = @lang_id


GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
