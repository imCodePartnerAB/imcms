package imcode.server.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
    Connection getConnection() throws SQLException;
    void testConnectionAndLoggResultToTheErrorLog();
}
