package imcode.server;

import imcode.util.Prefs;
import imcode.server.db.DBConnectionManager;
import imcode.server.db.NonPoolingDriverDBConnectionManager;
// import imcode.server.db.InetPoolManager;
import org.apache.log4j.Category;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *  Description of the Class
 *
 *@author     kreiger
 */
public class ApplicationServer {
    private final static String CVS_REV = "$Revision$";
    private final static String CVS_DATE = "$Date$";

    private final static String CONFIG_FILE = "ImcServer.cfg";

    private final Hashtable serverObjects = new Hashtable();

    private final static Category log = Category.getInstance( "ApplicationServer" );


    /**
     *  Constructor for the ApplicationServer object
     */
    public ApplicationServer() {

        // get list of servers
        StringTokenizer st = null;
        try {
            String servers = Prefs.get( "Servers", CONFIG_FILE );
            st = new StringTokenizer( servers, " ," );
            int serverObjectCount = st.countTokens();
            log.info( serverObjectCount + " Server" + (serverObjectCount == 1 ? ": " : "s: ") + servers );
        } catch( IOException ex ) {
            log.fatal( "Unable to load properties from " + CONFIG_FILE, ex );
            throw new RuntimeException( ex.getMessage() );
        } catch( NullPointerException ex ) {
            log.fatal( "Unable to load properties from " + CONFIG_FILE, ex );
            throw ex;
        }

        while( st.hasMoreTokens() ) {
            String servername = st.nextToken();
            log.info( "Reading properties for server " + servername );

            Properties serverprops = null;

            try {
                String serverpropsfile = Prefs.get( servername + ".properties", CONFIG_FILE );
                serverprops = Prefs.getProperties( serverpropsfile );
            } catch( IOException ex ) {
                log.fatal( "Unable to load properties for server " + servername, ex );
                continue;
            }

            // Find out what class this object is supposed to be of.
            String classname = serverprops.getProperty( "Class" );

            try {

                // Load the class
                Class objClass = Class.forName( classname );

                // Let's find the constructor that takes an "InetPoolManager" and a Properties.
                Constructor objConstructor = objClass.getConstructor( new Class[]{DBConnectionManager.class, Properties.class} );

                DBConnectionManager dbConnectionManager = createDBConnectionMananger( servername, serverprops );
                Object[] paramArr = {dbConnectionManager, serverprops};

                // Invoke Constructor(InetPoolManager, Properties) on class
                Object o = objConstructor.newInstance( paramArr );

                serverObjects.put( servername, o );

            } catch( ClassNotFoundException ex ) {
                log.fatal( "Unable to find class " + classname, ex );
            } catch( NoSuchMethodException ex ) {
                log.fatal( "Class " + classname + " does not have a compatible constructor " + classname + "(InetPoolManager, Properties)", ex );
            } catch( InstantiationException ex ) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            } catch( IllegalAccessException ex ) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            } catch( java.lang.reflect.InvocationTargetException ex ) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            }
        }

        log.info( "imcmsd started: " + new java.util.Date() );
        log.info( "imcmsd running..." );
    }

    private DBConnectionManager createDBConnectionMananger( String servername, Properties props ) {
        DBConnectionManager result = null;

        String jdbcDriver = props.getProperty( "JdbcDriver" );
        String jdbcUrl = props.getProperty( "Url" );
        String host = props.getProperty( "Host" );
        String databaseName = props.getProperty( "DatabaseName" );
        String port = props.getProperty( "Port" );
        String user = props.getProperty( "User" );
        String password = props.getProperty( "Password" );
        int maxConnectionCount = Integer.parseInt(props.getProperty( "MaxConnectionCount" ));

        /*
        log.debug( "Properties values for server '" + servername + "':");
        log.debug( "JdbcDriver=" + jdbcDriver );
        log.debug( "JdbcUrl=" + jdbcUrl );
        log.debug( "host=" + host );
        log.debug( "DatabaseName=" + databaseName );
        log.debug( "Port=" + port );
        log.debug( "User=" + user );
        log.debug( "Password=" + password );
        log.debug( "MaxConnectionCount=" + maxConnectionCount );
        */

        try {

            /* To use the old, commersical pooled driver uncomment this code, and comment out the other code following */
            /*
            result = new InetPoolManager( servername, ""+maxConnectionCount,
                                      host, port, databaseName,
                                      user, password, "30");
            */
            String serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;
            result = new NonPoolingDriverDBConnectionManager( servername, jdbcDriver, serverUrl, user, password, maxConnectionCount );
            result.testConnection();

        } catch( Exception ex ) {
            log.fatal( "Failed to create database connection pool");
            log.fatal( "Url = " + jdbcUrl );
            log.fatal( "Driver = " +jdbcDriver );
            log.fatal( "", ex );
        }

        return result;
    }


    // return server count
    /**
     *  Gets the serverCount attribute of the ApplicationServer object
     *
     *@return    The serverCount value
     */
    public int getServerCount() {
        return serverObjects.size();
    }


    /**
     *  Gets the serverObject attribute of the ApplicationServer object
     *
     *@param  serverObjectName  Description of Parameter
     *@return                   The serverObject value
     */
    public Object getServerObject( String serverObjectName ) {
        return serverObjects.get( serverObjectName );
    }

}
