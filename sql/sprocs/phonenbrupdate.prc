SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[PhoneNbrUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrUpdate]
GO


CREATE PROCEDURE PhoneNbrUpdate 
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
 @user_id int,
 @phone_id int,
 @nbr varchar(25),
 @phonetype_id int
AS
UPDATE phones
 SET number = @nbr, phonetype_id = @phonetype_id
WHERE phones.user_id = @user_id
AND phones.phone_id = @phone_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

