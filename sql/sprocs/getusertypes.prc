SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetUserTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserTypes]
GO


CREATE PROCEDURE GetUserTypes
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/
  @lang_prefix VARCHAR(3)
 AS
 SELECT DISTINCT user_type, type_name 
 FROM user_types
 WHERE lang_prefix=@lang_prefix


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

