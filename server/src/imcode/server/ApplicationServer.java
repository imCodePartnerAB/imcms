package imcode.server;

import imcode.server.db.ConnectionPool;
import imcode.server.db.ConnectionPoolForNonPoolingDriver;
import imcode.util.Prefs;
import org.apache.log4j.Category;

import java.io.IOException;
import java.util.Properties;

public class ApplicationServer {

    private final static Category log = Category.getInstance( "ApplicationServer" );

    private static IMCServiceInterface imcServiceInterface;


    public static IMCServiceInterface getIMCServiceInterface() throws IOException {
        if ( null == imcServiceInterface ) {
            imcServiceInterface = createIMCServiceInterface();
        }
        return imcServiceInterface;
    }

    private static IMCServiceInterface createIMCServiceInterface() throws IOException {
        Properties serverprops = Prefs.getProperties( "server.properties" );
        ConnectionPool connectionPool = createConnectionPool( serverprops );
        return new IMCService( connectionPool, serverprops );
    }

    private static ConnectionPool createConnectionPool( Properties props ) {
        ConnectionPool connectionPool = null;

        String jdbcDriver = props.getProperty( "JdbcDriver" );
        String jdbcUrl = props.getProperty( "Url" );
        String host = props.getProperty( "Host" );
        String databaseName = props.getProperty( "DatabaseName" );
        String port = props.getProperty( "Port" );
        String user = props.getProperty( "User" );
        String password = props.getProperty( "Password" );
        int maxConnectionCount = Integer.parseInt( props.getProperty( "MaxConnectionCount" ) );

        log.debug( "JdbcDriver = " + jdbcDriver );
        log.debug( "JdbcUrl = " + jdbcUrl );
        log.debug( "Host = " + host );
        log.debug( "DatabaseName = " + databaseName );
        log.debug( "Port = " + port );
        log.debug( "User = " + user );
        log.debug( "Password = " + password );
        log.debug( "MaxConnectionCount = " + maxConnectionCount );

        try {

            /* To use the old, commercial pooled driver uncomment this code, and comment out the other code following */
            /*
            connectionPool = new InetPoolManager( servername, ""+maxConnectionCount,
                                      host, port, databaseName,
                                      user, password, "30");
            */
            String serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;

            connectionPool = new ConnectionPoolForNonPoolingDriver( jdbcDriver, serverUrl, user, password, maxConnectionCount );
            connectionPool.testConnectionAndLogResultToTheErrorLog();


        } catch ( Exception ex ) {
            log.fatal( "Failed to create database connection pool" );
            log.fatal( "Url = " + jdbcUrl );
            log.fatal( "Driver = " + jdbcDriver );
            log.fatal( "", ex );
            throw new RuntimeException( ex );
        }

        return connectionPool;
    }

}
