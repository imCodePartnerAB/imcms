package imcode.server.db.exceptions;

import org.apache.log4j.Logger;

import java.sql.SQLException;

public class DatabaseException extends Exception {

    private final static Logger log = Logger.getLogger( DatabaseException.class.getName() );

    public DatabaseException( String message, Throwable ex ) {
        super( message, ex ) ;
    }

    public static DatabaseException fromSQLException( String message, SQLException cause ) {
        DatabaseException result ;
        String sqlState = cause.getSQLState();
        if ( "23000".equals( sqlState ) ) {
            result = new IntegrityConstraintViolationException( message, cause );
        } else if ( "01004".equals( sqlState ) ) {
            result = new StringTruncationException( message, cause );
        } else {
            if (null != sqlState) {
                log.debug( "SQLException with SQLState " + sqlState );
            }
            result = new DatabaseException( message, cause ) ;
        }
        result.setStackTrace( cause.getStackTrace() );
        return result ;
    }
}
