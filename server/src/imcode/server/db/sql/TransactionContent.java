package imcode.server.db.sql;

import java.sql.SQLException;

public interface TransactionContent {
    public void execute() throws SQLException;
}
