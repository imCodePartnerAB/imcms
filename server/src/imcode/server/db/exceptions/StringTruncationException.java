package imcode.server.db.exceptions;

import java.sql.SQLException;

public class StringTruncationException extends DatabaseException {

    StringTruncationException( String message, SQLException ex ) {
        super( message, ex);
    }
}
