DECLARE @string VARCHAR(15)


--Don't forget to change ip_end to ip_start too.
DECLARE my_curse CURSOR
FOR (SELECT ip_end FROM ip_accesses)

DECLARE @ipnum INT
DECLARE @index INT
DECLARE @oldindex INT
DECLARE @ip DECIMAL
DECLARE @exp INT

OPEN my_curse

FETCH NEXT FROM my_curse
INTO @string

WHILE @@FETCH_STATUS = 0 BEGIN
	SET @ip = 0
	SET @exp = 4
	SET @index = 1
	SET @oldindex = 1

	PRINT @string
	WHILE @index <= LEN(@string)+1 BEGIN
		IF SUBSTRING(@string,@index,1) = '.' OR @index = LEN(@string)+1  BEGIN
			SET @exp = @exp - 1
			SET @ipnum = CAST(SUBSTRING(@string,@oldindex,@index-@oldindex) AS INT)
			SET @oldindex = @index + 1
			SET @ip = @ip + POWER(256.0, @exp) * @ipnum
		END
		SET @index = @index+1
	END
	PRINT @ip

	UPDATE ip_accesses SET ip_end = @ip
	WHERE CURRENT OF my_curse

	FETCH NEXT FROM my_curse
	INTO @string
END
CLOSE my_curse
DEALLOCATE my_curse
