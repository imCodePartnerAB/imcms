package imcode.server.db;

import imcode.server.db.exceptions.IntegrityConstraintViolationSQLException;
import imcode.server.db.exceptions.StringTruncationSQLException;
import org.apache.log4j.Logger;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException {

    private final static Logger log = Logger.getLogger( DatabaseException.class.getName() );

    public DatabaseException( SQLException ex ) {
        super(ex);
    }

    public static DatabaseException from( SQLException ex ) {
        String sqlState = ex.getSQLState();
        if ( "23000".equals( sqlState ) ) {
            return new IntegrityConstraintViolationSQLException( ex );
        } else if ( "01004".equals( sqlState ) ) {
            return new StringTruncationSQLException( ex );
        } else {
            log.debug( "SQLException with SQLState " + sqlState );
        }
        return new DatabaseException( ex );
    }
}
