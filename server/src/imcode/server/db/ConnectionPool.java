package imcode.server.db;

import imcode.server.ApplicationServer;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class ConnectionPool {
    private static Logger log = Logger.getLogger( ConnectionPool.class);
    public abstract Connection getConnection() throws SQLException;
    public abstract void testConnectionAndLogResultToTheErrorLog() throws SQLException;

    public static ConnectionPool createConnectionPool(String jdbcUrl, String host, int port, String databaseName, String jdbcDriver, String user, String password, int maxConnectionCount) {
        ConnectionPool connectionPool;
        try {
            String serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;

            connectionPool = new ConnectionPoolForNonPoolingDriver( jdbcDriver, serverUrl, user, password, maxConnectionCount );
            connectionPool.testConnectionAndLogResultToTheErrorLog();

        } catch ( Exception ex ) {
            log.fatal( "Failed to create connection pool Url: "+jdbcUrl+" Driver: "+jdbcDriver, ex );
            throw new RuntimeException( ex );
        }
        return connectionPool;
    }
}
