package imcode.server.db;

import java.sql.SQLException;

public class StringTruncationSQLException extends RuntimeException {

    public StringTruncationSQLException( SQLException ex ) {
        super(ex);
    }
}
