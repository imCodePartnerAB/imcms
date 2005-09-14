package imcode.server.db.commands;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.StringUtils;

public class UpdateTableWhereColumnEqualsDatabaseCommand extends UpdateTableWithColumnValuesDatabaseCommand {

    private final String columnName;
    private Object columnValue;

    public UpdateTableWhereColumnEqualsDatabaseCommand(String tableName, String columnName,
                                                       Object columnValue, Object[][] columnNamesAndValues) {
        super(tableName, columnNamesAndValues);
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public Object executeOn(DatabaseConnection connection) throws DatabaseException {
        String[] columnNamePlaceHolderPairs = new String[columnNames.length];
        for ( int i = 0; i < columnNames.length; i++ ) {
            columnNamePlaceHolderPairs[i] = columnNames[i]+ " = ?";
        }
        Object[] parameters = new Object[columnValues.length+1];
        System.arraycopy(columnValues, 0, parameters, 0, columnValues.length);
        parameters[parameters.length-1] = columnValue ;
        return new Integer(connection.executeUpdate("UPDATE "+tableName+" SET "+ StringUtils.join(columnNamePlaceHolderPairs, ", ")+ " WHERE "+columnName +" = ?", parameters)) ;
    }
}
