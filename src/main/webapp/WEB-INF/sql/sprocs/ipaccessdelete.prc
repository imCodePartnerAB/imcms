CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM ip_accesses
WHERE ip_access_id = @ipAccessId
