SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetSessionCounterValue]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetSessionCounterValue]
GO

CREATE PROCEDURE SetSessionCounterValue
 @value int 
AS
 update sys_data
 set value = @value
 where type_id = 1
 
  return
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

