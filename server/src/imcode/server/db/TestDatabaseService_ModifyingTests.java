package imcode.server.db;

import java.sql.Timestamp;

import imcode.server.test.Log4JConfiguredTestCase;

/**
 * These tests is slow because we set up the database before every test
 */
public class TestDatabaseService_ModifyingTests extends Log4JConfiguredTestCase {
    private DatabaseService sqlServer;
    private DatabaseService mimer;
    private DatabaseService mySql;

    protected void setUp() {
        initMySql();
        initSqlServer();
        initMimer();
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

    public void test_sproc_phoneNbrAdd() {
        test_sproc_AddNewuser();
        int mimerRowAffected = mimer.sproc_phoneNbrAdd( 3, "1234567", 0 );
        int sqlServerRowAffected = sqlServer.sproc_phoneNbrAdd( 3, "1234567", 0 );
        int mySqlRowAffected = mySql.sproc_phoneNbrAdd( 3, "1234567", 0 );
        assertEquals( 1, mimerRowAffected );
        assertEquals( mimerRowAffected, sqlServerRowAffected );
        assertEquals( mimerRowAffected, mySqlRowAffected );
    }

    private void initMimer() {
        mimer = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        mimer.initializeDatabase();
    }

    private void initSqlServer() {
        sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        sqlServer.initializeDatabase();
    }

    private void initMySql() {
        mySql = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        mySql.initializeDatabase();
    }
}
