package imcode.server.db;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * The purpose of this class is to help JDBC drivers that lack Connection Pooling.
 * It does so by registrering with the DriverManager a new driver that has Connection Pooling that
 * in turn calls the non pooled driver.
 */
public class ConnectionPoolForNonPoolingDriver implements ConnectionPool {

    private final static String POOL_NAME = "imcode.server.db.ConnectionPoolForNonPoolingDriver";
    private final static String URI_FOR_POOLED_DRIVER = "jdbc:apache:commons:dbcp:";

    private ObjectPool connectionPool;
    private String userName;
    private String password;
    private String dbUrl;
    private Class nonPooledDriverClass;
    private int maxActiveConnections;

    private final static Logger log = Logger.getLogger( "imcode.server.db.ConnectionPoolForNonPoolingDriver" );

    public ConnectionPoolForNonPoolingDriver( String driverClassName, String dbUrl, String userName, String password, int maxActiveConnections ) throws Exception {
        this.userName = userName;
        this.password = password;
        this.dbUrl = dbUrl;
        this.maxActiveConnections = maxActiveConnections;

        // Load driver (and let itself register with the Driver Manager)
        this.nonPooledDriverClass = Class.forName( driverClassName );

        setupPoolingDriver();

        logDriverInfo( nonPooledDriverClass );
        Connection connection = getConnection();
        logDatabaseData( connection );
        connection.close();
    }

    private static String getUsedConnectionsString( ObjectPool connectionPool ) {
        return "Used: " + connectionPool.getNumActive() + "/" + ( connectionPool.getNumIdle() + connectionPool.getNumActive() );
    }

    public Connection getConnection() throws SQLException {
        Connection result = null;
        try {
            if ( log.isDebugEnabled() ) {
                log.debug( "Getting connection from pool. " + getUsedConnectionsString( connectionPool ) );
            }
            result = DriverManager.getConnection( URI_FOR_POOLED_DRIVER + dbUrl, userName, password );
            if ( log.isDebugEnabled() ) {
                log.debug( "Got connection from pool. " + getUsedConnectionsString( connectionPool ) );
            }
        } catch ( org.apache.commons.dbcp.DbcpException ex ) {
            log.error( getAttributeAsString(), ex );
            throw (SQLException)ex.getCause();
        }
        return result;
    }

    public void testConnectionAndLogResultToTheErrorLog() throws SQLException {
        getConnection().close();
        log.debug( getAttributeAsString(), null );
        log.info( "Test Connection OK" );
    }

    private String getAttributeAsString() {
        StringBuffer result = new StringBuffer();
        result.append( "dbUrl = " + dbUrl + "\n" );
        result.append( "userName = " + userName + "\n" );
        result.append( "password = " + password + "\n" );
        result.append( "nonPooledDriverClass = " + nonPooledDriverClass + "\n" );
        return result.toString();
    }

    private static void logDatabaseData( Connection connection ) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        log.info( "Database product version = " + metaData.getDatabaseProductVersion() );
    }

    private static void logDriverInfo( Class actualDriverClass ) throws InstantiationException, IllegalAccessException {
        Driver driver = (Driver)actualDriverClass.newInstance();
        log.info( "Driver Class = " + driver.getClass().getName() );
        log.info( "Driver version = " + driver.getMajorVersion() + "." + driver.getMinorVersion() );
    }

    /**
     * This method uses some stuff from the
     * @link http://jakarta.apache.org/
     * projekt Commons: commons-collections-2.1, commons-dbcp-1.0 and commons-pool-1.0.1
     */
    private Driver setupPoolingDriver() throws Exception {
        // This holds all connecions that are reused
        // Use AbandonedObjectPool instead? To be able to trace leaks
        connectionPool = new GenericObjectPool( null, maxActiveConnections );

        // This creates all the actual connections to the database
        DriverManagerConnectionFactory actualConnectionFactory = new DriverManagerConnectionFactory( dbUrl, userName, password );

        // This creates all wrapper connections that are pooled (and that uses the actual connections)
        new PoolableConnectionFactory( actualConnectionFactory, connectionPool, null, null, false, true );

        // It seems that it does some magic behind the scenes, tie itself to the pool I guess.
        // Anyhow, it needs to be instantiated.

        // The PoolingDriver acts like a normal JDBC driver and register itself with the java.sql.DriverManager
        // This meeans that now you can get Connections as you normaly would without having to bother
        // about the underliying pooling stuff
        // Allthoug you need to use a differens uri than you normaly wolud, see getConnection
        PoolingDriver result = new PoolingDriver();
        result.registerPool( dbUrl, connectionPool );

        return result;
    }
}
