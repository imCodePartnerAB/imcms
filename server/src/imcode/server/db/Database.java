package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;

public interface Database {

    Object executeCommand( DatabaseCommand databaseCommand ) throws DatabaseException;
}