package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;

public class TestDatabaseService extends Log4JConfiguredTestCase {
    static final String DB_HOST = "localhost";

    static final int SQLSERVER_PORT = 1433;
    static final String SQLSERVER_DATABASE_NAME = "test";
    static final String SQLSERVE_DATABASE_USER = "sa";
    static final String SQLSERVE_DATABASE_PASSWORD = "sa";

    static final int MIMER_PORT = 1360;
    static final String MIMMER_DATABASE_NAME = "test";
    static final String MIMMER_DATABASE_USER = "sysadm";
    static final String MIMMER_DATABASE_PASSWORD = "admin";

    static int MYSQL_PORT = 3306;
    static String MYSQL_DATABASE_NAME = "test";
    static String MYSQL_DATABASE_USER = "root";
    static String MYSQL_DATABASE_PASSWORD = "";

    private DatabaseService sqlServer;
    private DatabaseService mimer;
    private DatabaseService mySql;

    private final boolean testMimer = true;  // because it is so slow to test this database we need sometimes to turn those tests off.

    protected void setUp() {
        initMySql();
        initSqlServer();
        if( testMimer )
            initMimer();
    }

    private void nonmodifyingTestSameResultFrom_sproc_getAllRoles() {
        DatabaseService.Table_roles[] sqlServerRoles = sqlServer.sprocGetAllRoles_but_user();
        DatabaseService.Table_roles[] mySQLRoles = mySql.sprocGetAllRoles_but_user();
        assertEquals( 2, sqlServerRoles.length );
        assertEquals( 2, mySQLRoles.length );

        if( testMimer ) {
            DatabaseService.Table_roles[] mimerRoles = mimer.sprocGetAllRoles_but_user();
            assertEquals( 2, mimerRoles.length );
            static_assertEquals( mimerRoles, sqlServerRoles, mySQLRoles );
        }
    }

    private void nonmodifyingTestSameResultFrom_sproc_getAllUsers() {
        DatabaseService.Table_users[] sqlServerUsers = sqlServer.sprocGetAllUsers_OrderByLastName();
        DatabaseService.Table_users[] mySQLUsers = mySql.sprocGetAllUsers_OrderByLastName();
        assertEquals( 2, sqlServerUsers.length );
        assertEquals( 2, mySQLUsers.length );

        if( testMimer ) {
            DatabaseService.Table_users[] mimerUsers = mimer.sprocGetAllUsers_OrderByLastName();
            assertEquals( 2, mimerUsers.length );
            static_assertEquals( mimerUsers, sqlServerUsers, mySQLUsers );
        }
    }

    private void nonmodifyingTestSameResultFrom_sproc_getTemplatesInGroup() {
        DatabaseService.ViewTemplateGroup templateGroupZero = new DatabaseService.ViewTemplateGroup( 1, "Start" );

        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupZero = sqlServer.sprocGetTemplatesInGroup( 0 );
        assertEquals( 1, sqlServerTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, sqlServerTemplatesInGroupZero[0] );
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupOneo = sqlServer.sprocGetTemplatesInGroup( 1 );
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupTwo = sqlServer.sprocGetTemplatesInGroup( 2 );

        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupZero = mySql.sprocGetTemplatesInGroup( 0 );
        assertEquals( 1, mySQLTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, mySQLTemplatesInGroupZero[0] );
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupOne = mySql.sprocGetTemplatesInGroup( 1 );
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupTwo = mySql.sprocGetTemplatesInGroup( 2 );

        if( testMimer ) {
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupZero = mimer.sprocGetTemplatesInGroup( 0 );
            assertEquals( 1, mimerTemplatesInGroupZero.length );
            assertEquals( templateGroupZero, mimerTemplatesInGroupZero[0] );
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupOne = mimer.sprocGetTemplatesInGroup( 1 );
            assertEquals( mimerTemplatesInGroupOne.length, sqlServerTemplatesInGroupOneo.length );
            assertEquals( mimerTemplatesInGroupOne.length, mySQLTemplatesInGroupOne.length );
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupTwo = mimer.sprocGetTemplatesInGroup( 2 );
            assertEquals( mimerTemplatesInGroupTwo.length, sqlServerTemplatesInGroupTwo.length );
            assertEquals( mimerTemplatesInGroupTwo.length, mySQLTemplatesInGroupTwo.length );
        }
    }

    private void nonmodifyingTest_sproc_getHighestUserId() {
        if( testMimer ) {
            int mimerUserMax = mimer.sproc_getHighestUserId();
            assertEquals( 3, mimerUserMax );
        }

        int sqlServerUserMax = sqlServer.sproc_getHighestUserId();
        int mySqlUserMax = mySql.sproc_getHighestUserId();
        assertEquals( 3, sqlServerUserMax );
        assertEquals( 3, mySqlUserMax );
    }


    /** in order to speed up the testing a bit **/
    public void testAllNonModifyingTests() {
        nonmodifyingTest_sproc_getHighestUserId();
        nonmodifyingTestSameResultFrom_sproc_getAllRoles();
        nonmodifyingTestSameResultFrom_sproc_getAllUsers();
        nonmodifyingTestSameResultFrom_sproc_getTemplatesInGroup();
    }

    public void test_sproc_AddNewuser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = createDummyUser( nextFreeUserId );

        if( testMimer ) {
            DatabaseService.Table_users[] mimerUsersBefore = mimer.sprocGetAllUsers_OrderByLastName();
            mimer.sproc_AddNewuser( user );
            DatabaseService.Table_users[] mimerUsersAfter = mimer.sprocGetAllUsers_OrderByLastName();
            assertTrue( mimerUsersAfter.length == mimerUsersBefore.length + 1 );
        }

        DatabaseService.Table_users[] sqlServerUsersBefore = sqlServer.sprocGetAllUsers_OrderByLastName();
        sqlServer.sproc_AddNewuser( user );
        DatabaseService.Table_users[] sqlServerUsersAfter = sqlServer.sprocGetAllUsers_OrderByLastName();
        assertTrue( sqlServerUsersAfter.length == sqlServerUsersBefore.length + 1 );

        DatabaseService.Table_users[] mySqlUsersBefore = mySql.sprocGetAllUsers_OrderByLastName();
        mySql.sproc_AddNewuser( user );
        DatabaseService.Table_users[] mySqlUsersAfter = mySql.sprocGetAllUsers_OrderByLastName();
        assertTrue( mySqlUsersAfter.length == mySqlUsersBefore.length + 1 );
    }

    public void test_sproc_updateUser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = createDummyUser( nextFreeUserId );
        if( testMimer ) {
            mimer.sproc_AddNewuser( user );
            int mimerRowCount = mimer.sproc_updateUser( user );
            assertEquals( 1, mimerRowCount );
        }

        sqlServer.sproc_AddNewuser( user );
        mySql.sproc_AddNewuser( user );

        int sqlServerRowCount = sqlServer.sproc_updateUser( user );
        int mySqlRowCount = mySql.sproc_updateUser( user );

        assertEquals( 1, sqlServerRowCount );
        assertEquals( 1, mySqlRowCount );

        DatabaseService.Table_users[] sqlServerUsers = sqlServer.sprocGetAllUsers_OrderByLastName();
        DatabaseService.Table_users modifiedUser = null;
        int i = 0;
        while( modifiedUser == null ) {
            if( sqlServerUsers[i].user_id == nextFreeUserId ) {
                modifiedUser = sqlServerUsers[i];
            }
            i++;
        }
        assertEquals( user, modifiedUser );
    }

    public void test_sproc_delUser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = createDummyUser( nextFreeUserId );

        if( testMimer ) {
            test_sproc_delUsers( mimer, user, nextFreeUserId );
        }
        test_sproc_delUsers( mySql, user, nextFreeUserId );
        test_sproc_delUsers( sqlServer, user, nextFreeUserId );
    }

    private void test_sproc_delUsers( DatabaseService dbService, DatabaseService.Table_users user, int nextFreeUserId ) {
        DatabaseService.Table_users[] usersBefore = dbService.sprocGetAllUsers_OrderByLastName();
        dbService.sproc_AddNewuser( user );
        int rowsAffected = dbService.sproc_delUser( nextFreeUserId );
        assertTrue( rowsAffected > 0 );
        DatabaseService.Table_users[] usersAfter = dbService.sprocGetAllUsers_OrderByLastName();
        static_assertEquals( usersBefore, usersAfter );
    }

    public void test_sproc_phoneNbrAdd() {
        DatabaseService.Table_users user = createDummyUser( 3 );

        sqlServer.sproc_AddNewuser( user );
        int sqlServerRowCount = sqlServer.sproc_phoneNbrAdd( 3, "1234567", 0 );
        assertEquals( 1, sqlServerRowCount );

        mySql.sproc_AddNewuser( user );
        int mySqlRowCount = mySql.sproc_phoneNbrAdd( 3, "1234567", 0 );
        assertEquals( 1, mySqlRowCount );

        if( testMimer ) {
            mimer.sproc_AddNewuser( user );
            int mimerRowCount = mimer.sproc_phoneNbrAdd( 3, "1234567", 0 );
            assertEquals( 1, mimerRowCount );
        }
    }

    public void test_sproc_AddUseradminPermissibleRoles() {
        assertEquals( 1, sqlServer.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        assertEquals( 1, mySql.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        if( testMimer ) {
            assertEquals( 1, mimer.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        }
    }

    public void test_sproc_AddUserRole() {
        DatabaseService.Table_user_roles_crossref existing = new DatabaseService.Table_user_roles_crossref( 1, 0 );
        DatabaseService.Table_user_roles_crossref nonExisting = new DatabaseService.Table_user_roles_crossref( 1, 1 );

        test_sproc_AddUserRole( sqlServer, existing, nonExisting );
        test_sproc_AddUserRole( mySql, existing, nonExisting );
        if( testMimer ) {
            test_sproc_AddUserRole( mimer, existing, nonExisting );
        }
    }

    private void test_sproc_AddUserRole( DatabaseService dbService, DatabaseService.Table_user_roles_crossref existing, DatabaseService.Table_user_roles_crossref nonExisting ) {
        assertEquals( 0, dbService.sproc_AddUserRole( existing ) );
        assertEquals( 1, dbService.sproc_AddUserRole( nonExisting ) );
        assertEquals( 0, dbService.sproc_AddUserRole( nonExisting ) );
    }

    private DatabaseService.Table_users createDummyUser( int nextFreeUserId ) {
        DatabaseService.Table_users user = new DatabaseService.Table_users( nextFreeUserId, "test login name", "test password", "First name", "Last name", "Titel", "Company", "Adress", "City", "Zip", "Country", "Country council", "Email adress", 0, 1001, 0, 1, 1, 1, new Timestamp( new java.util.Date().getTime() ) );
        return user;
    }

    private static void static_assertEquals( Object[] ref, Object[] one, Object[] another ) {
        static_assertEquals( ref, one );
        static_assertEquals( ref, another );
    }

    private static void static_assertEquals( Object[] oneArr, Object[] anotherArr ) {
        if( oneArr == null ) {
            assertNotNull( anotherArr );
        } else if( anotherArr == null ) {
            fail( "The second array is null, but not the first oneArr" );
        } else {
            assertTrue( oneArr != anotherArr );
            assertEquals( oneArr.length, anotherArr.length );
            for( int i = 0; i < oneArr.length; i++ ) {
                Object one = oneArr[i];
                Object another = anotherArr[i];
                assertTrue( one != another );
                assertEquals( one, another );
            }
        }
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
