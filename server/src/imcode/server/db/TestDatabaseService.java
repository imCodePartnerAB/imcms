package imcode.server.db;

import junit.framework.TestCase;

public class TestDatabaseService extends TestCase {
    protected static final String DB_HOST = "localhost";
    protected static final int SQL_SERVER_PORT = 1433;
    protected static final String SQLSERVER_DATABASE_NAME = "test";
    protected static final String SQLSERVE_DATABASE_USER = "sa";
    protected static final String SQLSERVE_DATABASE_PASSWORD = "sa";
    protected static final int MIMER_PORT = 1360;
    protected static final String MIMMER_DATABASE_NAME = "test";
    protected static final String MIMMER_DATABASE_USER = "sysadm";
    protected static final String MIMMER_DATABASE_PASSWORD = "admin";

    protected static void initDatabases() throws Exception {
        DatabaseService sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQL_SERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        sqlServer.initializeDatabase();

        DatabaseService mimer = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        mimer.initializeDatabase();
    }
}
