package imcode.server.db;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {

    private static Logger log = Logger.getLogger( ConnectionPool.class );

    public abstract Connection getConnection() throws SQLException;

    protected abstract void testConnectionAndLogResultToTheErrorLog() throws SQLException;

    public static ConnectionPool createConnectionPool( String jdbcUrl, String host, int port, String databaseName,
                                                       String jdbcDriver, String user, String password,
                                                       int maxConnectionCount ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConnectionPool connectionPool;
        try {
            String serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;
            connectionPool = new ConnectionPoolForNonPoolingDriver( jdbcDriver, serverUrl, user, password, maxConnectionCount );
            connectionPool.testConnectionAndLogResultToTheErrorLog();

        } catch ( Exception ex ) {
            log.fatal( "Failed to create connection pool Url: " + jdbcUrl + " Driver: " + jdbcDriver, ex );
            throw new RuntimeException( ex );
        }
        stopWatch.stop();
        log.debug( "createConnectionPool() : " + stopWatch.getTime() + "ms" );
        return connectionPool;
    }
}
