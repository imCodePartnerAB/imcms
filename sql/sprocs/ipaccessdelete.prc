CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId


;
