CREATE PROCEDURE IPAccessUpdate
/*
Updates the IPaccess table
*/
 @IpAccessId int ,
 @newUserId int,
 @newIpStart DECIMAL ,
 @newIpEnd DECIMAL 
AS
UPDATE IP_ACCESSES
SET user_id = @newUserId ,
 ip_start = @newIpStart,
 ip_end = @newIpEnd
WHERE ip_access_id = @IpAccessId 


;
