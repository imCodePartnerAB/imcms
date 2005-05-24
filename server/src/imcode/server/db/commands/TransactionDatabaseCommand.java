package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
    An abstract DatabaseCommand that can be overridden to run something in an transaction. 
**/
public abstract class TransactionDatabaseCommand implements DatabaseCommand {

    Logger log = Logger.getLogger(TransactionDatabaseCommand.class) ;

    public Object executeOn( DatabaseConnection dc ) throws DatabaseException {
        try {
            Connection connection = dc.getConnection() ;
            try {
                connection.setAutoCommit( false );
                Object result = executeInTransaction( dc );
                connection.commit();
                return result;
            } catch ( Throwable t ) {
                log.debug("Rolling back transaction.", t) ;
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
