package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;

import java.util.*;

public class CompositeDatabaseCommand implements DatabaseCommand {

    private final List databaseCommands = new ArrayList();

    CompositeDatabaseCommand() { }

    CompositeDatabaseCommand( DatabaseCommand databaseCommand ) {
        add(databaseCommand) ;    
    }

    public CompositeDatabaseCommand( DatabaseCommand[] databaseCommands ) {
        this.databaseCommands.addAll( Arrays.asList( databaseCommands ) );
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        for ( Iterator iterator = databaseCommands.iterator(); iterator.hasNext(); ) {
            DatabaseCommand databaseCommand = (DatabaseCommand)iterator.next();
            databaseCommand.executeOn( connection );
        }
        return null;
    }

    public boolean add( Object o ) {
        return databaseCommands.add( o );
    }
}
