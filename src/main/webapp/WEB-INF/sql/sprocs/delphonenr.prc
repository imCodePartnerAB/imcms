CREATE PROCEDURE DelPhoneNr
 @aUserId int
AS
/**
	DOCME: Document me!
**/

 DELETE 
 FROM phones
 WHERE user_id = @aUserId
