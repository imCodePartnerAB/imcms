CREATE PROCEDURE GetUserFlagsForUser @user_id INT AS
/**
    Get a list of the flags for a single user
**/

SELECT	user_flags.user_flag_id,
		user_flags.name,
		user_flags.type,
		user_flags.description
FROM		user_flags,
		user_flags_crossref
WHERE		user_flags.user_flag_id = user_flags_crossref.user_flag_id
AND		user_flags_crossref.user_id = @user_id
;
