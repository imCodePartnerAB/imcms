package imcode.server;

import imcode.util.Prefs;
import imcode.server.db.*;
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
        } catch (IOException ex) {
            log.fatal( "Unable to load properties from " + CONFIG_FILE, ex );
            throw new RuntimeException( ex.getMessage() );
        } catch (NullPointerException ex) {
            log.fatal( "Unable to load properties from " + CONFIG_FILE, ex );
            throw ex;
        }

        while (st.hasMoreTokens()) {
            String servername = st.nextToken();
            log.info( "Reading properties for server " + servername );

            Properties serverprops = null;

            try {
                String serverpropsfile = Prefs.get( servername + ".properties", CONFIG_FILE );
                serverprops = Prefs.getProperties( serverpropsfile );
            } catch (IOException ex) {
                log.fatal( "Unable to load properties for server " + servername, ex );
                continue;
            }

            // Find out what class this object is supposed to be of.
            String classname = serverprops.getProperty( "Class" );

            try {

                // Load the class
                Class objClass = Class.forName( classname );

                // Let's find the constructor that takes an "InetPoolManager" and a Properties.
                Constructor objConstructor = objClass.getConstructor( new Class[]{DatabaseService.class, Properties.class} );

                DatabaseService databaseService = createDBConnectionMananger( servername, serverprops );
                Object[] paramArr = {databaseService, serverprops};

                // Invoke Constructor(InetPoolManager, Properties) on class
                Object o = objConstructor.newInstance( paramArr );

                serverObjects.put( servername, o );

            } catch (ClassNotFoundException ex) {
                log.fatal( "Unable to find class " + classname, ex );
            } catch (NoSuchMethodException ex) {
                log.fatal( "Class " + classname + " does not have a compatible constructor " + classname + "(InetPoolManager, Properties)", ex );
            } catch (InstantiationException ex) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            } catch (IllegalAccessException ex) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            } catch (java.lang.reflect.InvocationTargetException ex) {
                log.fatal( "Failed to invoke found constructor " + classname + "(InetPoolManager, Properties) on class " + classname, ex );
            }
        }

        log.info( "imcmsd started: " + new java.util.Date() );
        log.info( "imcmsd running..." );
    }

    private DatabaseService createDBConnectionMananger( String servername, Properties props ) {

        DatabaseService result = null;

        String databaseServiceClass = props.getProperty( "DatabaseServiceClass" );
        String host = props.getProperty( "Host" );
        String databaseName = props.getProperty( "DatabaseName" );
        String port = props.getProperty( "Port" );
        String user = props.getProperty( "User" );
        String password = props.getProperty( "Password" );
        int maxConnectionCount = Integer.parseInt( props.getProperty( "MaxConnectionCount" ) );

        log.debug( "Properties values for server '" + servername + "':" );
        log.debug( "DatabaseServiceClass=" + databaseServiceClass );
        log.debug( "host=" + host );
        log.debug( "DatabaseName=" + databaseName );
        log.debug( "Port=" + port );
        log.debug( "User=" + user );
        log.debug( "Password=" + password );
        log.debug( "MaxConnectionCount=" + maxConnectionCount );

        if ("imcode.server.db.MySQLDatabaseService".equals( databaseServiceClass )) {
            result = new MySQLDatabaseService( host, new Integer( port ), databaseName, user, password, new Integer( port ) );
        } else if ("imcode.server.db.MSSQLDatabaseService".equals( databaseServiceClass )) {
            result = new MSSQLDatabaseService( host, new Integer( port ), databaseName, user, password, new Integer( port ) );
        } else if ("imcode.server.db.MimerDatabaseService".equals( databaseServiceClass )) {
            result = new MimerDatabaseService( host, new Integer( port ), databaseName, user, password, new Integer( port ) );
        } else {
            log.fatal("The database of type " + databaseServiceClass + " is currently not supported. Misspelling?");
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
