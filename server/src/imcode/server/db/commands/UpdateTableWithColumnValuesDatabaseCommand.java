package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;

public abstract class UpdateTableWithColumnValuesDatabaseCommand implements DatabaseCommand {
    protected String tableName;
    protected String[] columnNames;
    protected Object[] columnValues;

    protected UpdateTableWithColumnValuesDatabaseCommand(String tableName, Object[][] columnNamesAndValues) {
        this.tableName = tableName;
        columnNames = new String[columnNamesAndValues.length] ;
        columnValues = new Object[columnNamesAndValues.length] ;
        for ( int i = 0; i < columnNamesAndValues.length; i++ ) {
            columnNames[i] = (String) columnNamesAndValues[i][0] ;
            columnValues[i] = columnNamesAndValues[i][1] ;
        }
    }
}
