package imcode.server.db.exceptions;

import java.sql.SQLException;

public class IntegrityConstraintViolationException extends DatabaseException {

    IntegrityConstraintViolationException( String message, SQLException ex ) {
        super(message, ex) ;
    }
}
