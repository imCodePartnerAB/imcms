package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;

public class TransactionWrappingDatabaseCommand extends TransactionDatabaseCommand {

    DatabaseCommand databaseCommand ;

    public TransactionWrappingDatabaseCommand( DatabaseCommand databaseCommand ) {
        this.databaseCommand = databaseCommand;
    }

    public Object executeInTransaction( DatabaseConnection connection ) {
        return databaseCommand.executeOn( connection ) ;
    }
}
