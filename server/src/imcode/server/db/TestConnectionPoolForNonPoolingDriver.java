package imcode.server.db;

import org.apache.log4j.*;

import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2003-sep-19
 * Time: 11:52:36
 * To change this template use Options | File Templates.
 */
public class TestConnectionPoolForNonPoolingDriver extends TestCase {

    private static Logger log = Logger.getLogger( "TestConnectionPoolForNonPoolingDriver" );

    private final static String DRIVER_CLASS_NAME = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    private String userName = System.getProperty( "test.db.user" ) ;
    private String passWord = System.getProperty( "test.db.pass" ) ;
    private String dbUrl ;

    static {
        Layout layout = new SimpleLayout() ;
        Appender appender = new ConsoleAppender(layout) ;
        appender.setName("System.out");

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);
        rootLogger.addAppender(appender);
    }

    public void setUp() throws Exception, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String serverName = System.getProperty( "test.db.host" );
        String serverPort = System.getProperty( "test.db.port" );
        String databaseName = System.getProperty( "test.db.database" );

        String[] args = { userName, passWord, serverName, serverPort, databaseName } ;
        if ( Arrays.asList( args ).contains( null ) ) {
            String usageInfo = "Setup system properties for this test as " +
                    "-Dtest.db.host=<host> " +
                    "-Dtest.db.port=<port> " +
                    "-Dtest.db.database=<database> " +
                    "-Dtest.db.user=<username> " +
                    "-Dtest.db.pass=<password>" ;
            log.fatal(usageInfo) ;
            System.exit(1) ;
        }
        dbUrl = "jdbc:microsoft:sqlserver://" + serverName + ":" + serverPort + ";DatabaseName=" + databaseName;

    }

    public void testFullPoolAndReturningOfConnectionWhenClose() throws Exception {
        int poolSize = 1;
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( DRIVER_CLASS_NAME, dbUrl, userName, passWord, poolSize );
        final Connection con1 = cm.getConnection();
        assertNotNull(con1) ;

        final int sleepTimeInMs = 500;
        final long sleepUntil = System.currentTimeMillis() + sleepTimeInMs ;
        new Thread( ) {
            public void run() {
                for (long sleepLeft = sleepUntil - System.currentTimeMillis() ; sleepLeft > 0 ; sleepLeft = sleepUntil - System.currentTimeMillis()) {
                    try {
                        Thread.sleep( sleepLeft );
                    } catch ( InterruptedException e ) {
                        log.warn(e) ;
                    }
                }
                try {
                    con1.close();
                } catch ( SQLException e ) {
                    fail() ;
                }
            }
        }.start();
        // This should hang until con1 is closed.
        Connection con2 = cm.getConnection();
        assertNotNull(con2) ;
        assertSame(con1, con2);
    }

    public void testTwoDifferentConnections() throws Exception {
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( DRIVER_CLASS_NAME, dbUrl, userName, passWord, 20 );
        Connection con1 = cm.getConnection();
        assertNotNull(con1) ;
        Connection con2 = cm.getConnection();
        assertNotNull(con2) ;
        assertNotSame(con1,con2);
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testConnectionWithSQLDriverOnly() throws ClassNotFoundException, SQLException {
        Class.forName( DRIVER_CLASS_NAME );
        Connection con = DriverManager.getConnection( dbUrl, userName, passWord );
        assertNotNull(con) ;
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testConnectionWithPool() throws Exception {
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( DRIVER_CLASS_NAME, dbUrl, userName, passWord, 20 );
        try {
            cm.testConnectionAndLogResultToTheErrorLog();
        } catch (SQLException ex) {
            fail() ;
        }
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testCallStoredProcedureWithParam() throws Exception {
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( DRIVER_CLASS_NAME, dbUrl, userName, passWord, 20 );
        Connection conn = cm.getConnection();
        CallableStatement cs = conn.prepareCall( "{call " + "GetTextDocData" + "(?) }" );
        cs.setString( 1, "1001" );
        ResultSet rs = cs.executeQuery();
        while ( rs.next() ) {
            String templateId = rs.getString( 1 );
            assertEquals(templateId,"1") ;
        }
        conn.close();
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testListAllTables() throws Exception {
        DatabaseMetaData metaData = getConnectionMetaData( DRIVER_CLASS_NAME, dbUrl, userName, passWord );
        String[] types = {"TABLE"};
        ResultSet resultSet = metaData.getTables( null, null, "%", types );
        Collection tableNames = new ArrayList();
        while ( resultSet.next() ) {
            String tableName = resultSet.getString( 3 );
            tableNames.add( tableName );
        }
        assertFalse(tableNames.isEmpty());
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testListAllStoredProcedures() throws Exception {
        DatabaseMetaData metaData = getConnectionMetaData( DRIVER_CLASS_NAME, dbUrl, userName, passWord );
        ResultSet rs = metaData.getProcedures( null, null, "%" );
        Collection procedureNames = new ArrayList();
        while ( rs.next() ) {
            String procedureName = rs.getString( 3 );
            procedureNames.add( procedureName );
        }
        assertFalse(procedureNames.isEmpty()) ;
    }

    /**
     * Only used for testing, see main-method above
     */
    private static DatabaseMetaData getConnectionMetaData( String driverClassName, String dbUrl, String userName, String passWord ) throws Exception {
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( driverClassName, dbUrl, userName, passWord, 20 );
        Connection con = cm.getConnection();
        DatabaseMetaData metaData = con.getMetaData();
        return metaData;
    }

}
