package imcode.server.db;
import imcode.server.test.Log4JConfiguredTestCase;
import org.apache.log4j.Logger;

public class TestDatabaseService extends Log4JConfiguredTestCase {
    protected static final String DB_HOST = "localhost";

    protected static final int SQLSERVER_PORT = 1433;
    protected static final String SQLSERVER_DATABASE_NAME = "test";
    protected static final String SQLSERVE_DATABASE_USER = "sa";
    protected static final String SQLSERVE_DATABASE_PASSWORD = "sa";

    protected static final int MIMER_PORT = 1360;
    protected static final String MIMMER_DATABASE_NAME = "test";
    protected static final String MIMMER_DATABASE_USER = "sysadm";
    protected static final String MIMMER_DATABASE_PASSWORD = "admin";

    private static int MYSQL_PORT = 3306;
    private static String MYSQL_DATABASE_NAME = "test";
    private static String MYSQL_DATABASE_USER = "root";
    private static String MYSQL_DATABASE_PASSWORD = "";

    static DatabaseService sqlServer;
    static DatabaseService mimer;
    static DatabaseService mySql;

    private static Logger log = Logger.getLogger( TestDatabaseService.class );

    static {
        try {
            initAllDatabases();
        } catch( Exception ex ) {
            log.error( "", ex );
        }
    }

    protected static void initAllDatabases() throws Exception {
        initMySql();
        initSqlServer();
        initMimer();
    }

    private static void initMimer() throws Exception {
        mimer = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        mimer.initializeDatabase();
    }

    private static void initSqlServer() throws Exception {
        sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        sqlServer.initializeDatabase();
    }

    private static void initMySql() throws Exception {
        mySql = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        mySql.initializeDatabase();
    }
}
