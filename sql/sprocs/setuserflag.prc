CREATE PROCEDURE SetUserFlag @user_id INT, @flag_name VARCHAR(64) AS

INSERT INTO 	user_flags_crossref 
SELECT	@user_id, user_flags.user_flag_id
FROM 		user_flags
WHERE	name = @flag_name
