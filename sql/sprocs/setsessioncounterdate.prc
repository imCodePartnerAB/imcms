SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetSessionCounterDate]
;


CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
/**
	DOCME: Document me!
**/

      
 update sys_data
 set value = @new_date where type_id = 2
 
  return


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

