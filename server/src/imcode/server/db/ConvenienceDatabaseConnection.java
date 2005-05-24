package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.commands.QueryDatabaseCommand;
import imcode.server.db.handlers.FlatStringArrayResultSetHandler;
import imcode.server.db.handlers.MultiStringArrayResultSetHandler;
import imcode.server.db.handlers.SingleStringResultSetHandler;

public class ConvenienceDatabaseConnection extends DatabaseConnectionWrapper {
    public ConvenienceDatabaseConnection(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public String[] executeArrayQuery( final String sql, final String[] parameters ) throws DatabaseException {
        return (String[])(new QueryDatabaseCommand( sql, parameters, new FlatStringArrayResultSetHandler() ) ).executeOn(this);
    }

    public String[][] execute2dArrayQuery(String sql, String[] parameters) throws DatabaseException {
        return (String[][]) (new QueryDatabaseCommand( sql, parameters, new MultiStringArrayResultSetHandler()) ).executeOn(this);
    }

    public String executeStringQuery(String sql, String[] parameters) {
        return (String) (new QueryDatabaseCommand( sql, parameters, new SingleStringResultSetHandler()) ).executeOn(this);
    }


}
