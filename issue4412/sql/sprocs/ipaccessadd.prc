CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/
 @user_id int,
 @ip_start DECIMAL , 
 @ip_end DECIMAL
AS
INSERT INTO ip_accesses ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )
