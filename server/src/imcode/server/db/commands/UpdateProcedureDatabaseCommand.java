package imcode.server.db.commands;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

public class UpdateProcedureDatabaseCommand extends ProcedureDatabaseCommand {

    public UpdateProcedureDatabaseCommand( String procedure, String[] params ) {
        super(procedure, params);
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        return new Integer(connection.executeUpdateProcedure( procedure, params )) ;
    }
}
