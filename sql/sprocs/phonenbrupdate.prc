SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[PhoneNbrUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrUpdate]
;


CREATE PROCEDURE PhoneNbrUpdate 
/*
This function updates a phone number in db. Used by AdminUserProps
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


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

