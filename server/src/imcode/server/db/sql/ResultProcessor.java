package imcode.server.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultProcessor {
    public abstract Object mapOneRow( ResultSet rs ) throws SQLException;
}

