package imcode.server;

import imcode.server.db.ConnectionPool;
import imcode.util.Prefs;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class ApplicationServer {

    private final static Logger log = Logger.getLogger( imcode.server.ApplicationServer.class.getName() );
    private static IMCServiceInterface imcServiceInterface ;
    private static ConnectionPool apiConnectionPool ;

    public synchronized static IMCServiceInterface getIMCServiceInterface() {
        if ( null == imcServiceInterface ) {
            imcServiceInterface = createIMCServiceInterface();
        }
        return imcServiceInterface;
    }

    private static IMCServiceInterface createIMCServiceInterface() {
        Properties serverprops = getServerProperties();
        ConnectionPool connectionPool = createConnectionPool( serverprops );
        IMCService imcref = new IMCService( connectionPool, serverprops );
        return imcref ;
    }

    public synchronized static ConnectionPool getApiConnectionPool() {
        if (null == apiConnectionPool) {
            Properties serverprops = getServerProperties();
            apiConnectionPool = createConnectionPool( serverprops );
        }
        return apiConnectionPool ;
    }

    private static Properties getServerProperties() {
        Properties serverprops = null;
        try {
            serverprops = Prefs.getProperties( "server.properties" );
        } catch ( IOException e ) {
            log.fatal( "Failed to initialize imCMS", e);
            throw new RuntimeException(e) ;
        }
        return serverprops;
    }

    private static ConnectionPool createConnectionPool( Properties props ) {
        ConnectionPool connectionPool = null;

        String jdbcDriver = props.getProperty( "JdbcDriver" );
        String jdbcUrl = props.getProperty( "Url" );
        String host = props.getProperty( "Host" );
        String databaseName = props.getProperty( "DatabaseName" );
        int port = Integer.parseInt(props.getProperty( "Port" ));
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

        connectionPool = ConnectionPool.createConnectionPool(jdbcUrl, host, port, databaseName, jdbcDriver, user, password, maxConnectionCount);

        return connectionPool;
    }

}
