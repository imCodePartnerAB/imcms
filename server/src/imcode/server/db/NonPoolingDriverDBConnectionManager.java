package imcode.server.db;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.log4j.Category;

import java.sql.*;

public class NonPoolingDriverDBConnectionManager implements DBConnectionManager {
    private String serverName;
    private String userName;
    private String password;
    private String dbUrl;
    private Class actualDriverClass;
    private String pooledDataSourceName;

    private final static String POOLED_DATA_SOURCE_NAME_PREFIX = "imcode.server.db.";
    private final String URI_FOR_POOLED_DRIVER = "jdbc:apache:commons:dbcp:";

    private static Category log = Category.getInstance( "NonPoolingDriverDBConnectionManager" );
    private int maxActiveConnections;

    public NonPoolingDriverDBConnectionManager( String serverName, String driverClassName, String dbUrl,
                                                String userName, String password,
                                                int maxActiveConnections ) throws Exception, InstantiationException, SQLException, ClassNotFoundException {
        this.pooledDataSourceName = POOLED_DATA_SOURCE_NAME_PREFIX + serverName;
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
        this.dbUrl = dbUrl;
        this.maxActiveConnections = maxActiveConnections;

        this.actualDriverClass = registerActualDBDriver( driverClassName );
        setupPoolingDriver( dbUrl, userName, password );

        Driver driver = (Driver)actualDriverClass.newInstance();
        logInfo( "Driver Class = " + driver.getClass().getName() );
        logInfo( "Driver version = " + driver.getMajorVersion() + "." + driver.getMinorVersion() );
        DatabaseMetaData metaData = getConnection().getMetaData();
        logInfo( "Database product version = " + metaData.getDatabaseProductVersion() );
    }

    public Connection getConnection() throws SQLException {
        Connection result = null;
        try {
            result = getPooledConnection();
        } catch( org.apache.commons.dbcp.DbcpException ex ) {
            logDebug( attributeLoggString() );
            logDebug( "", ex);
            throw (SQLException)ex.getCause();
        }
        return result;
    }

    public void testConnection() {
        try {
            Connection result = getConnection();
            logDebug( attributeLoggString() );
            logInfo( "Test Connection OK" );
        } catch( SQLException e ) {
            logInfo( "Failed test to get connectcion ", e );
        }
    }

    private String attributeLoggString() {
        StringBuffer result = new StringBuffer();
        result.append( "ServerName = " + serverName + "\n");
        result.append( "dbUrl = " + dbUrl + "\n");
        result.append(  "userName = " + userName + "\n" );
        result.append( "password = " + password + "\n" );
        result.append( "actualDriverClass = " + actualDriverClass + "\n" );
        return result.toString();
    }

    private static Class registerActualDBDriver( String driverClassName ) throws ClassNotFoundException {
        return Class.forName( driverClassName );
    }

    private Connection getPooledConnection() throws SQLException {
        Connection result = DriverManager.getConnection( URI_FOR_POOLED_DRIVER + pooledDataSourceName, userName, password );
        return result;
    }

    private Driver setupPoolingDriver( String dbUrl, String userName, String password ) throws Exception {
        // This holds all connecions that are reused
        GenericObjectPool connectionPool = new GenericObjectPool( null ); // Use AbandonedObjectPool instead? To be able to trace leaks

        // This creates all the actual connections to the database
        DriverManagerConnectionFactory actualConnectionFactory = new DriverManagerConnectionFactory( dbUrl, userName, password );

        // This creates all wrapper connections that are pooled (and that uses the actual connections)
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory( actualConnectionFactory, connectionPool, null, null, false, true );
        // It seems that it does some magic behind the scenes, tie itself to the pool I guess.

        // The PoolingDriver acts like a normal JDBC driver and register itself with the java.sql.DriverManager
        // This meeans that now you can get Connections as you normaly would without having to bother
        // about the underliying pooling stuff
        // Allthoug you need to use a differens uri than you normaly wolud, see getConnection
        PoolingDriver result = new PoolingDriver();
        result.registerPool( pooledDataSourceName, connectionPool );
        return result;
    }

    private static void logInfo( String message ) {
        logInfo( message, null );
    }

    private static void logInfo( String message, Exception ex) {
        log( message, ex );
        log.info( message, ex );
    }

    private static void logDebug( String message ) {
        logDebug( message, null );
    }

    private static void logDebug( String message, Exception ex) {
        log( message, ex );
        log.info( message, ex );
    }

    private static void log( String message, Exception ex ) {
        System.out.println( message );
        if( ex != null ) {
            System.out.println( ex.getMessage() );
        }
    }
}
