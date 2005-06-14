package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;

public class InsertIntoTableDatabaseCommand implements DatabaseCommand {
    private String tableName;
    private String[] columnNames;
    private String[] columnValues;

    public InsertIntoTableDatabaseCommand(String tableName) {
        this.tableName = tableName;
    }

    public InsertIntoTableDatabaseCommand(String tableName, String[][] columnNamesAndValues) {
        this(tableName) ;
        columnNames = new String[columnNamesAndValues.length] ;
        columnValues = new String[columnNamesAndValues.length] ;
        for ( int i = 0; i < columnNamesAndValues.length; i++ ) {
            columnNames[i] = columnNamesAndValues[i][0] ;
            columnValues[i] = columnNamesAndValues[i][1] ;
        }
    }

    public Object executeOn(DatabaseConnection connection) throws DatabaseException {
        String sqlPlaceHolders = "?" + StringUtils.repeat(",?", columnNames.length - 1);
        return connection.executeUpdateAndGetGeneratedKey("INSERT INTO "+tableName+" ( "+ StringUtils.join(columnNames, ", ")+" ) VALUES ("+sqlPlaceHolders+")", columnValues) ;
    }
}
