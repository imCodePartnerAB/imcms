package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

import java.util.*;

public class CompositeDatabaseCommand extends TransactionDatabaseCommand {

    private final List databaseCommands = new ArrayList();

    public CompositeDatabaseCommand() {}

    public CompositeDatabaseCommand( DatabaseCommand[] databaseCommands ) {
        this.databaseCommands.addAll( Arrays.asList( databaseCommands ) );
    }

    public CompositeDatabaseCommand(DatabaseCommand databaseCommand) {
        add(databaseCommand);
    }

    public void add(DatabaseCommand databaseCommand) {
        databaseCommands.add(databaseCommand);
    }

    public Object executeInTransaction( DatabaseConnection connection ) throws DatabaseException {
        for ( Iterator iterator = databaseCommands.iterator(); iterator.hasNext(); ) {
            DatabaseCommand databaseCommand = (DatabaseCommand)iterator.next();
            databaseCommand.executeOn( connection );
        }
        return null;
    }



}
