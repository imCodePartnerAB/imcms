package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

public class UpdateDatabaseCommand implements DatabaseCommand {

    private final String sqlStr;
    private final Object[] parameters;

    public UpdateDatabaseCommand( String sqlStr, Object[] parameters ) {
        this.sqlStr = sqlStr;
        this.parameters = parameters;
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        return new Integer( connection.executeUpdate( sqlStr, parameters ) );
    }
}
