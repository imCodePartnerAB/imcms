package imcode.server.db.exceptions;

import imcode.server.db.DatabaseException;

import java.sql.SQLException;

public class IntegrityConstraintViolationSQLException extends DatabaseException {

    public IntegrityConstraintViolationSQLException( SQLException ex ) {
        super(ex) ;
    }
}
