package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;

public class UpdateDatabaseCommand implements DatabaseCommand {

    private final String sqlStr;
    private final String[] parameters;

    public UpdateDatabaseCommand( String sqlStr, String[] parameters ) {
        this.sqlStr = sqlStr;
        this.parameters = parameters;
    }

    public Object executeOn( DatabaseConnection connection ) {
        return new Integer( connection.executeUpdate( sqlStr, parameters ) );
    }
}
