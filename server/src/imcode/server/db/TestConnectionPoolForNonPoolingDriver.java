package imcode.server.db;

import org.apache.log4j.*;

import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileInputStream;

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

    private String driverClassName ;
    private String userName ;
    private String passWord ;
    private String dbUrl ;

    private static final String DATABASE_PROPERTIES_SYSTEM_PROPERTY = "test.db.properties";
    private static final String DEFAULT_DATABASE_PROPERTIES_FILE = "build.properties";

    static {
        Layout layout = new SimpleLayout() ;
        Appender appender = new ConsoleAppender(layout) ;
        appender.setName("System.out");

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.WARN);
        rootLogger.addAppender(appender);
    }

    public void setUp() throws Exception, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Properties dbProperties = new Properties() ;
        dbProperties.load(new FileInputStream( System.getProperty(DATABASE_PROPERTIES_SYSTEM_PROPERTY, DEFAULT_DATABASE_PROPERTIES_FILE ))) ;
        String serverName = dbProperties.getProperty( "db-host" );
        String serverPort = dbProperties.getProperty( "db-port" );
        String databaseName = dbProperties.getProperty( "db-name" );
        userName = dbProperties.getProperty( "db-user" ) ;
        passWord = dbProperties.getProperty( "db-pass" ) ;
        driverClassName = dbProperties.getProperty( "db-driver") ;
        dbUrl = dbProperties.getProperty( "db-url") ;

        dbUrl += serverName + ':' + serverPort + ";DatabaseName=" + databaseName;

    }

    public void testFullPoolAndReturningOfConnectionWhenClose() throws Exception {
        int poolSize = 1;
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( driverClassName, dbUrl, userName, passWord, poolSize );
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
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( driverClassName, dbUrl, userName, passWord, 20 );
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
        Class.forName( driverClassName );
        Connection con = DriverManager.getConnection( dbUrl, userName, passWord );
        assertNotNull(con) ;
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testConnectionWithPool() throws Exception {
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( driverClassName, dbUrl, userName, passWord, 20 );
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
        ConnectionPoolForNonPoolingDriver cm = new ConnectionPoolForNonPoolingDriver( driverClassName, dbUrl, userName, passWord, 20 );
        Connection conn = cm.getConnection();
        CallableStatement cs = conn.prepareCall( "{call " + "GetTextDocData" + "(?) }" );
        cs.setString( 1, "1001" );
        ResultSet rs = cs.executeQuery();
        while ( rs.next() ) {
            String templateId = rs.getString( 1 );
            assertNotNull( templateId ) ;
        }
        conn.close();
    }

    /**
     * Only used for testing, see main-method above
     */
    public void testListAllTables() throws Exception {
        DatabaseMetaData metaData = getConnectionMetaData( driverClassName, dbUrl, userName, passWord );
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
        DatabaseMetaData metaData = getConnectionMetaData( driverClassName, dbUrl, userName, passWord );
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
