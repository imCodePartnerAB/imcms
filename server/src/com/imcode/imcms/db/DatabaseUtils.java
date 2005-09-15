package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.db.handlers.ObjectArrayResultSetHandler;
import com.imcode.db.handlers.ObjectFromFirstRowResultSetHandler;
import com.imcode.db.handlers.ObjectFromRowFactory;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    public static String executeStringQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String)database.executeCommand( new SqlQueryDatabaseCommand( sql, parameters, new ObjectFromFirstRowResultSetHandler(new StringFromRowFactory()) ) );
    }

    public static String[] executeStringArrayQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String[])database.executeCommand( new SqlQueryDatabaseCommand( sql, parameters, new StringArrayResultSetHandler() ) );
    }

    public static String[][] execute2dStringArrayQuery( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return (String[][])database.executeCommand( new SqlQueryDatabaseCommand( sql, parameters, new StringArrayArrayResultSetHandler() ) );
    }

    public static int executeUpdate( Database database, final String sql, final Object[] parameters ) throws DatabaseException {
        return ((Integer)database.executeCommand( new SqlUpdateDatabaseCommand( sql, parameters ) )).intValue();
    }

    public static Object executeObjectQuery(Database database, String sql, String[] parameters,
                                            ObjectFromRowFactory objectFromRowFactory) {
        return database.executeCommand(new SqlQueryDatabaseCommand(sql, parameters, new ObjectFromFirstRowResultSetHandler(objectFromRowFactory)));
    }

    public static Object[] executeObjectArrayQuery(Database database, String sql, String[] parameters,
                                                   ObjectFromRowFactory objectFromRowFactory) {
        return (Object[])database.executeCommand(new SqlQueryDatabaseCommand(sql, parameters, new ObjectArrayResultSetHandler(objectFromRowFactory)));
    }

}
