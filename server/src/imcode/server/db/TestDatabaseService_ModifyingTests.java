package imcode.server.db;

import java.sql.Timestamp;

/**
 * These tests is slow because we set up the database before every test
 */
public class TestDatabaseService_ModifyingTests extends TestDatabaseService {
    DatabaseService sqlServer;
    DatabaseService mimer;
    DatabaseService mySql;

    protected void setUp() throws Exception {
        initAllDatabases();
    }

    public void test_sproc_AddNewuser() {
        DatabaseService.Table_users[] mimerUsersBefore = mimer.sprocGetAllUsers();
        DatabaseService.Table_users[] sqlServerUsersBefore = sqlServer.sprocGetAllUsers();
        DatabaseService.Table_users[] mySqlUsersBefore = mySql.sprocGetAllUsers();

        int nextFreeUserId = 3;
        DatabaseService.Table_users user = new DatabaseService.Table_users(
            nextFreeUserId,
            "test login name",
            "test password",
            "First name",
            "Last name",
            "Titel",
            "Company",
            "Adress",
            "City",
            "Zip",
            "Country",
            "Country council",
            "Email adress",
            0,
            1001,
            0,
            1,
            1,
            1,
            new Timestamp( new java.util.Date().getTime() )
        );

        mimer.sproc_AddNewuser( user );
        sqlServer.sproc_AddNewuser( user );
        mySql.sproc_AddNewuser( user );

        DatabaseService.Table_users[] mimerUsersAfter = mimer.sprocGetAllUsers();
        DatabaseService.Table_users[] sqlServerUsersAfter = sqlServer.sprocGetAllUsers();
        DatabaseService.Table_users[] mySqlUsersAfter = mySql.sprocGetAllUsers();

        assertTrue( mimerUsersAfter.length == mimerUsersBefore.length + 1);
        assertTrue( sqlServerUsersAfter.length == sqlServerUsersBefore.length + 1);
        assertTrue( mySqlUsersAfter.length == mySqlUsersBefore.length + 1);
    }

    protected void initAllDatabases() throws Exception {
        initMySql();
        initSqlServer();
        initMimer();
    }

    private void initMimer() throws Exception {
        mimer = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        mimer.initializeDatabase();
    }

    private void initSqlServer() throws Exception {
        sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        sqlServer.initializeDatabase();
    }

    private void initMySql() throws Exception {
        mySql = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        mySql.initializeDatabase();
    }
}
