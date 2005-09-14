package imcode.server.db;

import imcode.server.db.commands.QueryDatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.handlers.SingleStringResultSetHandler;
import imcode.server.db.handlers.StringArrayArrayResultSetHandler;
import imcode.server.db.handlers.StringArrayResultSetHandler;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static String[] executeStringArrayQuery( DatabaseConnection connection, final String sql, final String[] parameters ) throws DatabaseException {
        return (String[])(new QueryDatabaseCommand( sql, parameters, new StringArrayResultSetHandler() ) ).executeOn(connection);
    }

    public static String[][] execute2dStringArrayQuery(DatabaseConnection connection, String sql, String[] parameters) throws DatabaseException {
        return (String[][]) (new QueryDatabaseCommand( sql, parameters, new StringArrayArrayResultSetHandler()) ).executeOn(connection);
    }

    public static String executeStringQuery(DatabaseConnection connection, String sql, String[] parameters) {
        return (String) (new QueryDatabaseCommand( sql, parameters, new SingleStringResultSetHandler()) ).executeOn(connection);
    }
}
