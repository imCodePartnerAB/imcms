package imcode.server.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {

    private static Logger log = Logger.getLogger( ConnectionPool.class );

    public abstract Connection getConnection() throws SQLException;

    public static ConnectionPool createConnectionPool( String jdbcDriver, String jdbcUrl,
                                                       String user, String password,
                                                       int maxConnectionCount ) {
        ConnectionPool connectionPool;
        try {
            connectionPool = new ConnectionPoolForNonPoolingDriver( jdbcDriver, jdbcUrl, user, password, maxConnectionCount );
        } catch ( Exception ex ) {
            log.fatal( "Failed to create connection pool. Url: " + jdbcUrl + " Driver: " + jdbcDriver, ex );
            throw new RuntimeException( ex );
        }
        return connectionPool;
    }

    public abstract void destroy() ;
}
