
DECLARE @idstart INT
DECLARE @suffixmax INT
DECLARE @suffixmin INT
DECLARE @prefix VARCHAR(10)
DECLARE @password VARCHAR(10)
DECLARE @role_id INT

SET @suffixmin = 1
SET @suffixmax = 200
SET @idstart = 12000
SET @prefix='User'
SET @password = 'password'
SET @role_id = 7

DECLARE @index INT
SET @index = 0

DECLARE @user VARCHAR(15)

WHILE (@index+@suffixmin) <= @suffixmax
BEGIN
	PRINT @index + @suffixmin
	SET @user = @prefix+LTRIM(RTRIM(STR(@index+@suffixmin)))

	INSERT INTO users VALUES (@idstart+@index, @user, @password,@user, @user,'--','--','--','--','--','--','--','--',0,1001,0,1,1,1,GETDATE())
	INSERT INTO user_roles_crossref VALUES (@idstart+@index, @role_id)
	SET @index = @index + 1

END
