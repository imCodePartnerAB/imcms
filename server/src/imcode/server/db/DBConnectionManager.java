package imcode.server.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnectionManager {
    Connection getConnection() throws SQLException;
    void testConnectionAndLogResultToTheErrorLog();
}
