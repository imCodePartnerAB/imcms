package imcode.server.db;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is purpose is to help JDBC drivers that lacks Connection Pooling.
 * It does so by registrer with the DriverManager a new driver that has Connection Pooling that
 * in turn calls the non pooled driver.
 */
public class NonPoolingDriverDBConnectionManager {
    private final static String POOLED_DATA_SOURCE_NAME_PREFIX = "imcode.server.db.";
    private final static String URI_FOR_POOLED_DRIVER = "jdbc:apache:commons:dbcp:";

    private String serverName;
    private String userName;
    private String password;
    private String dbUrl;
    private Class nonPooledDriverClass;
    private String pooledDataSourceName;
    private int maxActiveConnections;

    private static Logger log = new Logger();

    /**
     * Created an own logger that logs both as the "standard" way in imcms and to standard output
     * so the class can be tested without needing to set up the loging environment
     */
    private static class Logger {
        private static Category log = Category.getInstance( "NonPoolingDriverDBConnectionManager" );

        void debug( String message, Exception ex ) {
            log.debug( message, ex );
            logToStandardOutput( message, ex );
        }

        void info( String message ) {
            log.debug( message );
            logToStandardOutput( message, null );
        }

        private static void logToStandardOutput( String message, Exception ex ) {
            System.out.println( message );
            if( ex != null ) {
                System.out.println( ex.getMessage() );
            }
        }
    }

    private NonPoolingDriverDBConnectionManager( String serverName, String driverClassName, String dbUrl, String userName, String password, int maxActiveConnections ) throws Exception, InstantiationException, SQLException, ClassNotFoundException {
        this.pooledDataSourceName = POOLED_DATA_SOURCE_NAME_PREFIX + serverName;
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
        this.dbUrl = dbUrl;
        this.maxActiveConnections = maxActiveConnections;

        // Load driver (and let itself register with the Driver Manager)
        this.nonPooledDriverClass = Class.forName( driverClassName );

        setupPoolingDriver();

        s_logDriverInfo( nonPooledDriverClass );
        s_logDatabaseData( getConnection() );
    }

    private Connection getConnection() throws SQLException {
        Connection result = null;
        try {
            Connection result11 = DriverManager.getConnection( URI_FOR_POOLED_DRIVER + pooledDataSourceName, userName, password );
            result = result11;
        } catch( org.apache.commons.dbcp.DbcpException ex ) {
            log.debug( getAttributeAsString(), ex );
            throw (SQLException)ex.getCause();
        }
        return result;
    }

    private void testConnectionAndLogResultToTheErrorLog() {
        try {
            getConnection();
            log.debug( getAttributeAsString(), null );
            log.info( "Test Connection OK" );
        } catch( SQLException e ) {
            log.debug( "Failed test to get connectcion ", e );
        }
    }

    private String getAttributeAsString() {
        StringBuffer result = new StringBuffer();
        result.append( "ServerName = " + serverName + "\n" );
        result.append( "dbUrl = " + dbUrl + "\n" );
        result.append( "userName = " + userName + "\n" );
        result.append( "password = " + password + "\n" );
        result.append( "nonPooledDriverClass = " + nonPooledDriverClass + "\n" );
        return result.toString();
    }

    private static void s_logDatabaseData( Connection con ) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        log.info( "Database product version = " + metaData.getDatabaseProductVersion() );
    }

    private static void s_logDriverInfo( Class actualDriverClass ) throws InstantiationException, IllegalAccessException {
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
        // +1 is a bug fix
        GenericObjectPool connectionPool = new GenericObjectPool( null, maxActiveConnections+1 ); // Use AbandonedObjectPool instead? To be able to trace leaks

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
        result.registerPool( pooledDataSourceName, connectionPool );
        return result;
    }

    /**
     * In order to test this class without needing to set up the logging facility, this method is used
     */
    /**
     * This main is used to test that the pooling and driver work properly. it can be run standalone, ie without beeing
     * run in an web application.
     */
    public static void main( String[] args ) throws Exception, ClassNotFoundException {
        String driverClassName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        String serverName = "localhost";
        String serverPort = "1433";
        String dbUrl = "jdbc:microsoft:sqlserver://" + serverName + ":" + serverPort + ";DatabaseName=hasse_db";

        s_testConnectionWithSQLDriverOnly( driverClassName, dbUrl );
        s_testConnectionWithPool( driverClassName, dbUrl );
        s_testCallStoredProcedureWithParam( driverClassName, dbUrl );
        s_testListAllTables( driverClassName, dbUrl );
        s_testListAllStoredProcedures( driverClassName, dbUrl );
        s_testTwoDifferentConnections( driverClassName, dbUrl );
        s_testFullPoolAndReturningOfConnectionWhenClose( driverClassName, dbUrl );
    }

    private static void s_testFullPoolAndReturningOfConnectionWhenClose( String driverClassName, String dbUrl ) throws Exception {
        int poolSize = 1;
        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "InternalServerName", driverClassName, dbUrl, "sa", "sa", poolSize );
        final Connection con1 = cm.getConnection();
        System.out.println( con1 );

        final int pauseInMs = 3000;
        new Thread( new Runnable() {
            public void run() {
                int waited = 0;
                int pause = 500;
                System.out.println("Nu väntar vi " + pauseInMs + " ms");
                while( waited <= pauseInMs ) {
                    waited += pause;
                    try {
                        Thread.sleep( pause );
                        System.out.print(".");
                    } catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
                System.out.println("Stänger con1, nu borde den lämnas åter till poolen");
                try {
                    con1.close();
                } catch( SQLException e ) {
                    e.printStackTrace();
                }
            }
        } ).start();
        System.out.println("Den borde hänga " + pauseInMs +  " ms på detta meddelande");
        Connection con2 = cm.getConnection();
        System.out.println( con2 );
        System.out.println("Och con1 == con2 borde vara true: " + (con1==con2));
    }

    private static void s_testTwoDifferentConnections( String driverClassName, String dbUrl ) throws Exception {
        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "InternalServerName", driverClassName, dbUrl, "sa", "sa", 20 );
        Connection con1 = cm.getConnection();
        System.out.println( con1 );
        Connection con2 = cm.getConnection();
        System.out.println( con2 );
        System.out.println( "De borde vara olika: " + (con1 != con2) );
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_testConnectionWithSQLDriverOnly( String driverClassName, String url ) throws ClassNotFoundException, SQLException {
        Class.forName( driverClassName );
        Connection con = DriverManager.getConnection( url, "sa", "sa" );
        System.out.println( con.toString() );
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_testConnectionWithPool( String driverClassName, String dbUrl ) throws Exception {
        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "InternalServerName", driverClassName, dbUrl, "sa", "sa", 20 );
        cm.testConnectionAndLogResultToTheErrorLog();
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_testCallStoredProcedureWithParam( String driverClassName, String dbUrl ) throws Exception {
        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "InternalServerName", driverClassName, dbUrl, "sa", "sa", 20 );
        Connection conn = cm.getConnection();
        CallableStatement cs = conn.prepareCall( "{call " + "GetTextDocData" + "(?) }" );
        cs.setString( 1, "1001" );
        ResultSet rs = cs.executeQuery();
        while( rs.next() ) {
            String str = rs.getString( 1 );
            System.out.println( str );
        }
        conn.close();
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_testListAllTables( String driverClassName, String dbUrl ) throws Exception {
        DatabaseMetaData metaData = s_getConnectionMetaData( driverClassName, dbUrl );
        String[] types = {"TABLE"};
        ResultSet resultSet = metaData.getTables( null, null, "%", types );
        Collection c = new ArrayList();
        while( resultSet.next() ) {
            String tableName = resultSet.getString( 3 );
            c.add( tableName );
        }
        String[] tabelNames = (String[])c.toArray( new String[c.size()] );
        s_writeToSystemOut( tabelNames );
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_testListAllStoredProcedures( String driverClassName, String dbUrl ) throws Exception {
        DatabaseMetaData metaData = s_getConnectionMetaData( driverClassName, dbUrl );
        ResultSet rs = metaData.getProcedures( null, null, "%" );
        Collection procedureNames = new ArrayList();
        while( rs.next() ) {
            String dbProcedureName = rs.getString( 3 );
            procedureNames.add( dbProcedureName );
        }

        String[] procNames = (String[])procedureNames.toArray( new String[procedureNames.size()] );
        s_writeToSystemOut( procNames );
    }

    /**
     * Only used for testing, se main-method above
     */
    private static DatabaseMetaData s_getConnectionMetaData( String driverClassName, String dbUrl ) throws Exception {
        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "InterntServerNamn", driverClassName, dbUrl, "sa", "sa", 20 );
        Connection con = cm.getConnection();
        DatabaseMetaData metaData = con.getMetaData();
        return metaData;
    }

    /**
     * Only used for testing, se main-method above
     */
    private static void s_writeToSystemOut( String[] strings ) {
        for( int i = 0; i < strings.length; i++ ) {
            System.out.println( strings[i] );
        }
    }
}
