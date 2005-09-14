package imcode.server.db;

import imcode.server.db.commands.QueryDatabaseCommand;
import imcode.server.db.commands.QueryProcedureDatabaseCommand;
import imcode.server.db.commands.UpdateDatabaseCommand;
import imcode.server.db.commands.UpdateProcedureDatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.handlers.ObjectArrayResultSetHandler;
import imcode.server.db.handlers.ObjectFromFirstRowResultSetHandler;
import imcode.server.db.handlers.SingleStringResultSetHandler;
import imcode.server.db.handlers.StringArrayArrayResultSetHandler;
import imcode.server.db.handlers.StringArrayResultSetHandler;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    public static String executeStringQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String)database.executeCommand( new QueryDatabaseCommand( sql, parameters, new SingleStringResultSetHandler() ) );
    }

    public static String[] executeStringArrayQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String[])database.executeCommand( new QueryDatabaseCommand( sql, parameters, new StringArrayResultSetHandler() ) );
    }

    public static String[][] execute2dStringArrayQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String[][])database.executeCommand( new QueryDatabaseCommand( sql, parameters, new StringArrayArrayResultSetHandler() ) );
    }

    public static int executeUpdate( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return ((Integer)database.executeCommand( new UpdateDatabaseCommand( sql, parameters ) )).intValue();
    }

    public static int executeUpdateProcedure( Database database, final String procedure, final Object[] parameters ) throws DatabaseException {
        return ((Integer)database.executeCommand( new UpdateProcedureDatabaseCommand( procedure, parameters ) )).intValue() ;
    }

    public static String executeStringProcedure( Database database, final String procedure, final Object[] parameters ) throws DatabaseException {
        return (String)database.executeCommand( new QueryProcedureDatabaseCommand( procedure, parameters, new SingleStringResultSetHandler() ) ) ;
    }

    public static String[] executeStringArrayProcedure( Database database, final String procedure, final Object[] parameters ) throws DatabaseException {
        return (String[])database.executeCommand( new QueryProcedureDatabaseCommand( procedure, parameters, new StringArrayResultSetHandler() ) ) ;
    }

    public static String[][] execute2dStringArrayProcedure( Database database, final String procedure, final Object[] parameters ) throws DatabaseException {
        return (String[][])database.executeCommand( new QueryProcedureDatabaseCommand( procedure, parameters, new StringArrayArrayResultSetHandler() ) ) ;
    }

    public static Object executeObjectQuery(Database database, String sql, String[] parameters,
                                            ObjectFromRowFactory objectFromRowFactory) {
        return database.executeCommand(new QueryDatabaseCommand(sql, parameters, new ObjectFromFirstRowResultSetHandler(objectFromRowFactory)));
    }

    public static Object[] executeObjectArrayQuery(Database database, String sql, String[] parameters,
                                                  ObjectFromRowFactory objectFromRowFactory) {
        return (Object[])database.executeCommand(new QueryDatabaseCommand(sql, parameters, new ObjectArrayResultSetHandler(objectFromRowFactory)));
    }
}
