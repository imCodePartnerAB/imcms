package imcode.server;

import imcode.server.db.ConnectionPool;
import imcode.util.Prefs;
import org.apache.log4j.Logger;
import org.apache.commons.lang.UnhandledException;

import java.io.IOException;
import java.util.Properties;

public class ApplicationServer {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    private final static Logger log = Logger.getLogger( imcode.server.ApplicationServer.class.getName() );
    private final static IMCServiceInterface imcServiceInterface = createIMCServiceInterface();

    private static ConnectionPool apiConnectionPool ;

    private ApplicationServer() {
    }

    public synchronized static IMCServiceInterface getIMCServiceInterface() {
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
        try {
            return Prefs.getProperties( SERVER_PROPERTIES_FILENAME );
        } catch ( IOException e ) {
            log.fatal( "Failed to initialize imCMS", e);
            throw new UnhandledException(e) ;
        }
    }

    private static ConnectionPool createConnectionPool( Properties props ) {

        String jdbcDriver = props.getProperty( "JdbcDriver" );
        String jdbcUrl = props.getProperty( "JdbcUrl" );
        String user = props.getProperty( "User" );
        String password = props.getProperty( "Password" );
        int maxConnectionCount = Integer.parseInt( props.getProperty( "MaxConnectionCount" ) );

        log.debug( "JdbcDriver = " + jdbcDriver );
        log.debug( "JdbcUrl = " + jdbcUrl );
        log.debug( "User = " + user );
        log.debug( "MaxConnectionCount = " + maxConnectionCount );

        return ConnectionPool.createConnectionPool( jdbcDriver, jdbcUrl, user, password, maxConnectionCount );
    }

}
