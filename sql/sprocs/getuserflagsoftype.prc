CREATE PROCEDURE GetUserFlagsOfType @type INT AS
/**
    Get a list of all user flags of the given type
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags
WHERE	user_flags.type = @type
;
