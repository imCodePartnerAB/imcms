package imcode.server.db.sql;

import com.inet.pool.PDataSource;
import com.inet.pool.PoolManager;
import org.apache.log4j.Category;

import javax.sql.ConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;


public class InetPoolManager implements ConnectionPool {

    private PoolManager manager;
    private ConnectionPoolDataSource ds;

    private static Category log = Category.getInstance( "InetPoolManager" );

    private String maxConnectionCount;
    private String dbServerName;
    private String databaseName;
    private String port;
    private String user;
    private String password;
    private String loginTimeout;

    /**
     * @deprecated Use other constructor
     */
    public InetPoolManager( String serverName, Properties props ) throws SQLException {
        this( serverName,
              props.getProperty( "MaxConnectionCount" ),
              props.getProperty( "ServerName" ),
              props.getProperty( "Port" ),
              props.getProperty( "DatabaseName" ),
              props.getProperty( "User" ),
              props.getProperty( "Password" ),
              props.getProperty( "LoginTimeout" ) );
    }

    private InetPoolManager( String serverName, String maxConnectionCount,
                            String dbServerName, String port, String databaseName,
                            String user, String password, String loginTimeout ) throws SQLException {
        this.maxConnectionCount = maxConnectionCount;
        this.dbServerName = dbServerName;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.loginTimeout = loginTimeout;

        PDataSource pds = createConnectionPool();
        setDataSourceProperties( pds );
        ds = pds;

        testConnectionAndLogMetaData( serverName );
    }

    private void setDataSourceProperties( PDataSource pds ) {
        try {
            pds.setServerName( dbServerName );
        } catch( NullPointerException ex ) {
            log.error( "Failed to find ServerName!" );
            throw ex;
        }
        log.info( "ServerName: " + pds.getServerName() );

        try {
            pds.setPort( port );
        } catch( NullPointerException ignored ) {
            // ignored
        }
        log.info( "Port: " + pds.getPort() );

        try {
            pds.setDatabaseName( databaseName );
        } catch( NullPointerException ex ) {
            log.error( "Failed to find DatabaseName!" );
            throw ex;
        }
        log.info( "DatabaseName: " + pds.getDatabaseName() );

        try {
            pds.setUser( user );
        } catch( NullPointerException ex ) {
            log.error( "Failed to find User!" );
            throw ex;
        }
        log.info( "User: " + pds.getUser() );

        try {
            pds.setPassword( password );
        } catch( NullPointerException ex ) {
            log.error( "Failed to find Password!" );
            throw ex;
        }

        try {
            pds.setLoginTimeout( Integer.parseInt( loginTimeout ) );
        } catch( NumberFormatException ex ) {
            log.debug( "Failed to parse LoginTimeout" );
        }

        log.info( "LoginTimeout: " + pds.getLoginTimeout() );
    }

    private PDataSource createConnectionPool() {
        manager = new PoolManager();
        try {
            manager.setMaxConnectionCount( Integer.parseInt( maxConnectionCount ) );
        } catch( NumberFormatException ignored ) {
            // ignored
        }
        log.info( "MaxConnectionCount: " + manager.getMaxConnectionCount() );

        // Create the DataSource.
        PDataSource pds = new PDataSource();
        return pds;
    }

    private void testConnectionAndLogMetaData( String serverName ) throws SQLException {
        Connection connection = null;
        try {
            // request the Connection
            connection = getConnection();

            //to get the driver version
            DatabaseMetaData conMD = connection.getMetaData();

            log.info( "Driver Name: " + conMD.getDriverName() );
            log.info( "Driver Version:" + conMD.getDriverVersion() );

            connection.close();
        } catch( SQLException ex ) {
            String errorMessageStr = "Failed to make first contact with the DataSource for server-object " + serverName + " (" + user + "@" + dbServerName + ":" + port + "/" + databaseName + ")";
            log.fatal( errorMessageStr );
            throw ex;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return manager.getConnection( ds );
        } catch( SQLException ex ) {
            String err = "Failed to get connection from pool: " + user + "@" + dbServerName + ":" + port + "/" + databaseName + " Connections Used: " + getUsedConnectionCount() + "/" + getMaxConnectionCount();

            log.warn( err );
            throw ex;
        }
    }

    /**
     return used connections
     */
    private int getUsedConnectionCount() {
        return manager.getUsedConnectionCount();
    }

    /**
     return max connections
     */
    private int getMaxConnectionCount() {
        return manager.getMaxConnectionCount();
    }
}
