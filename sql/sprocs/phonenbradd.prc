SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[phoneNbrAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[phoneNbrAdd]
GO


CREATE PROCEDURE phoneNbrAdd
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
 @user_id int,
 @country varchar(15) ,
 @area varchar(15) , 
 @nbr varchar(15)
AS
DECLARE @newPhoneId int
SELECT @newPhoneId = MAX(phone_id) + 1
FROM phones
IF @newPhoneId IS NULL 
 SET @newPhoneId = 1
INSERT INTO PHONES ( phone_id , country_code, area_code, number , user_id )
VALUES (@newPhoneId , @country, @area,  @nbr, @user_id )  


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

