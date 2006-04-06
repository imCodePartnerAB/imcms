CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
/**
	DOCME: Document me!
**/

      
 update sys_data
 set value = @new_date where type_id = 2
