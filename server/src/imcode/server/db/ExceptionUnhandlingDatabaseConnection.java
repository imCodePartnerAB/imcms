package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;

import java.sql.Connection;

public class ExceptionUnhandlingDatabaseConnection {

    DatabaseConnection wrappedConnection ;

    public ExceptionUnhandlingDatabaseConnection( DatabaseConnection connection ) {
        this.wrappedConnection = connection;
    }

    public Object executeProcedure( String procedure, String[] params, ResultSetHandler resultSetHandler ) {
        try {
            return wrappedConnection.executeProcedure( procedure, params, resultSetHandler );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public Object executeQuery( String sqlQuery, String[] parameters, ResultSetHandler resultSetHandler ) {
        try {
            return wrappedConnection.executeQuery( sqlQuery, parameters, resultSetHandler );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public int executeUpdate( String sql, Object[] parameters ) {
        try {
            return wrappedConnection.executeUpdate( sql, parameters );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters ) {
        try {
            return wrappedConnection.executeUpdateAndGetGeneratedKey( sql, parameters );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public int executeUpdateProcedure( String procedure, String[] parameters ) {
        try {
            return wrappedConnection.executeUpdateProcedure( procedure, parameters );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public Connection getConnection() {
        return wrappedConnection.getConnection();
    }

}
