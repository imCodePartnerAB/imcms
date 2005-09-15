package com.imcode.imcms.db;

import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.handlers.ObjectFromFirstRowResultSetHandler;

public class DatabaseConnectionUtils {

    private DatabaseConnectionUtils() {
    }

    public static String[] executeStringArrayQuery( DatabaseConnection connection, final String sql, final String[] parameters ) throws DatabaseException {
        return (String[])(new SqlQueryDatabaseCommand( sql, parameters, new StringArrayResultSetHandler() ) ).executeOn(connection);
    }

    public static String[][] execute2dStringArrayQuery(DatabaseConnection connection, String sql, String[] parameters) throws DatabaseException {
        return (String[][]) (new SqlQueryDatabaseCommand( sql, parameters, new StringArrayArrayResultSetHandler()) ).executeOn(connection);
    }

    public static String executeStringQuery(DatabaseConnection connection, String sql, String[] parameters) {
        return (String) (new SqlQueryDatabaseCommand( sql, parameters, new ObjectFromFirstRowResultSetHandler(new StringFromRowFactory())) ).executeOn(connection);
    }
}
