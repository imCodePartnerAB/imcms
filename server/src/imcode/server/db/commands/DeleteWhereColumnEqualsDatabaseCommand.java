package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

public class DeleteWhereColumnEqualsDatabaseCommand implements DatabaseCommand {

    private final String table;
    private final String column;
    private final Object columnValue;

    public DeleteWhereColumnEqualsDatabaseCommand( String table, String column, Object columnValue ) {
        this.table = table;
        this.column = column;
        this.columnValue = columnValue;
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        connection.executeUpdate( "DELETE FROM " + table + " WHERE " + column
                                  + " = ?", new Object[] {columnValue} );
        return null;
    }
}
