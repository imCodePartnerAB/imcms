package imcode.server.db.exceptions;

import imcode.server.db.DatabaseException;

import java.sql.SQLException;

public class StringTruncationSQLException extends DatabaseException {

    public StringTruncationSQLException( SQLException ex ) {
        super(ex);
    }
}
