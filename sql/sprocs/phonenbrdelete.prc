CREATE PROCEDURE PhoneNbrDelete
/*
 Deletes a phone number for a user.
*/
 @phoneId int
AS
DELETE FROM PHONES 
WHERE phone_id = @phoneId


;
