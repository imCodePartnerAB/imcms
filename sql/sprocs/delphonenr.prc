SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[DelPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelPhoneNr]
;


CREATE PROCEDURE DelPhoneNr
 @aUserId int
AS
/**
	DOCME: Document me!
**/

 DELETE 
 FROM phones
 WHERE user_id = @aUserId


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

