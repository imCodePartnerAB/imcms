CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name


;
