package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.UnhandledException;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class TransactionDatabaseCommand implements DatabaseCommand {

    public Object executeOn( DatabaseConnection dc ) throws DatabaseException {
        try {
            Connection connection = dc.getConnection() ;
            try {
                connection.setAutoCommit( false );
                Object result = executeInTransaction( dc );
                connection.commit();
                return result;
            } catch ( Throwable t ) {
                connection.rollback();
                if (t instanceof RuntimeException) {
                    throw (RuntimeException)t ;
                } else {
                    throw new UnhandledException( t ) ;
                }
            } finally {
                connection.setAutoCommit( true );
            }
        } catch ( SQLException e ) {
            throw DatabaseException.fromSQLException( null, e );
        }
    }

    public abstract Object executeInTransaction( DatabaseConnection connection ) throws DatabaseException;

}
