package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;

public interface DatabaseCommand {

    Object executeOn( DatabaseConnection connection ) throws DatabaseException ;

}
