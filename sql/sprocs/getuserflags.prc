CREATE PROCEDURE GetUserFlags AS
/**
    Get a list of all user flags
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags
;
