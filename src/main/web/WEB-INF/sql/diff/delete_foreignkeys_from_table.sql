DECLARE @table_name = ?

DECLARE constraints_cursor CURSOR FOR
SELECT constraints.name FROM sysobjects tables,
                             sysobjects constraints,
                             sysforeignkeys foreignkeys
WHERE  tables.name    = @table_name
AND    tables.id      = foreignkeys.fkeyid
AND    constraints.id = foreignkeys.constid

OPEN constraints_cursor

DECLARE @constraint_name VARCHAR(256)
FETCH NEXT FROM constraints_cursor
INTO @constraint_name

WHILE @@FETCH_STATUS = 0
BEGIN

    EXEC('ALTER TABLE '+@table_name+' DROP CONSTRAINT '+@constraint_name)

    FETCH NEXT FROM constraints_cursor
    INTO @constraint_name

END

CLOSE constraints_cursor
DEALLOCATE constraints_cursor
