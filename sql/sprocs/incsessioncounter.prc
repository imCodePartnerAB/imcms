SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[IncSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IncSessionCounter]
GO


CREATE PROCEDURE IncSessionCounter 
AS
/**
	DOCME: Document me!
**/

      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

