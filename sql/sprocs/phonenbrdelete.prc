SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[PhoneNbrDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrDelete]
;


CREATE PROCEDURE PhoneNbrDelete
/*
 Deletes a phone number for a user.
*/
 @phoneId int
AS
DELETE FROM PHONES 
WHERE phone_id = @phoneId


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

