CREATE PROCEDURE UnsetUserFlag @user_id INT, @flag_name VARCHAR(64) AS

DELETE FROM		user_flags_crossref
WHERE		user_id = @user_id
		AND	user_flag_id
			IN (
				SELECT	user_flag_id
				FROM 		user_flags
				WHERE 	name = @flag_name
			)
;
