SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetSessionCounterValue]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetSessionCounterValue]
;

CREATE PROCEDURE SetSessionCounterValue
 @value int 
AS
 update sys_data
 set value = @value
 where type_id = 1
 
  return
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

