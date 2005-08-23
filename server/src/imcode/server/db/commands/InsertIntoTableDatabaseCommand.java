package imcode.server.db.commands;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;

public class InsertIntoTableDatabaseCommand extends UpdateTableWithColumnValuesDatabaseCommand {

    public InsertIntoTableDatabaseCommand(String tableName, Object[][] columnNamesAndValues) {
        super(tableName, columnNamesAndValues);
    }

    public Object executeOn(DatabaseConnection connection) throws DatabaseException {
        String sqlPlaceHolders = "?" + StringUtils.repeat(",?", columnNames.length - 1);
        return connection.executeUpdateAndGetGeneratedKey("INSERT INTO "+tableName+" ( "+ StringUtils.join(columnNames, ", ")+" ) VALUES ("+sqlPlaceHolders+")", columnValues) ;
    }
}
