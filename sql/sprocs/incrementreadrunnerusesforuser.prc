CREATE PROCEDURE IncrementReadrunnerUsesForUser @user_id INT AS
/**
	Increment the uses of readrunner for one user.
	Parameters:
		@user_id  The id of the user
**/
UPDATE readrunner_user_data
SET uses = uses + 1
WHERE user_id = @user_id
