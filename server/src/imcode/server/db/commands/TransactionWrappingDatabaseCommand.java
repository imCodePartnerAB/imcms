package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

public class TransactionWrappingDatabaseCommand extends TransactionDatabaseCommand {

    DatabaseCommand databaseCommand ;

    public TransactionWrappingDatabaseCommand( DatabaseCommand databaseCommand ) {
        this.databaseCommand = databaseCommand;
    }

    public Object executeInTransaction( DatabaseConnection connection ) throws DatabaseException {
        return databaseCommand.executeOn( connection ) ;
    }
}
