package imcode.server.db;

import java.sql.SQLException;

public class IntegrityConstraintViolationSQLException extends RuntimeException {

    public IntegrityConstraintViolationSQLException( SQLException ex ) {
        super(ex) ;
    }
}
