package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class TransactionDatabaseCommand implements DatabaseCommand {

    public Object executeOn( DatabaseConnection dc ) {
        try {
            Connection connection = dc.getConnection() ;
            try {
                connection.setAutoCommit( false );
                Object result = executeInTransaction( dc );
                connection.commit();
                return result;
            } catch ( RuntimeException t ) {
                connection.rollback();
                throw t ;
            } finally {
                connection.setAutoCommit( true );
            }
        } catch ( SQLException e ) {
            throw DatabaseException.from( e );
        }
    }

    public abstract Object executeInTransaction( DatabaseConnection connection ) ;

}
