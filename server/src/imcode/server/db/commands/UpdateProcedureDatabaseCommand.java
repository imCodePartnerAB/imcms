package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;

public class UpdateProcedureDatabaseCommand implements DatabaseCommand {

    private final String procedure;
    private final String[] params;

    public UpdateProcedureDatabaseCommand( String procedure, String[] params ) {
        this.procedure = procedure;
        this.params = params;
    }

    public Object executeOn( DatabaseConnection connection ) {
        return new Integer(connection.executeUpdateProcedure( procedure, params )) ;
    }
}
