SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[phoneNbrAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[phoneNbrAdd]
GO


CREATE PROCEDURE phoneNbrAdd
/*
This function adds a new phone numbers to the db. Used by AdminUserProps
*/
 @user_id int,
 @nbr varchar(25),
 @phonetype_id int
AS
DECLARE @newPhoneId int
SELECT @newPhoneId = MAX(phone_id) + 1
FROM phones
IF @newPhoneId IS NULL 
 SET @newPhoneId = 1
INSERT INTO PHONES ( phone_id , number , user_id, phonetype_id )
VALUES (@newPhoneId , @nbr, @user_id, @phonetype_id )  


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

