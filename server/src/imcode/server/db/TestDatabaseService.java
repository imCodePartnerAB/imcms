package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;

public class TestDatabaseService extends Log4JConfiguredTestCase {

    private final boolean testMimer = true;  // because it is so slow to test this database we need sometimes to turn those tests off.


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

    protected void setUp() {
        mySql = static_initMySql();
        sqlServer = static_initSqlServer();
        if( testMimer )
            mimer = static_initMimer();
    }

    /** in order to speed up the testing a bit, i clumped together all the "SELECT" cases in one test **/
    public void testAllNonModifyingTests() {
        nonmodifyingTest_sproc_getHighestUserId();
        nonmodifyingTestSameResultFrom_sproc_getAllRoles();
        nonmodifyingTestSameResultFrom_sproc_getAllUsers();
        nonmodifyingTestSameResultFrom_sproc_getTemplatesInGroup();
    }

    private void nonmodifyingTestSameResultFrom_sproc_getAllRoles() {
        DatabaseService.Table_roles[] sqlServerRoles = sqlServer.sproc_GetAllRoles_but_user();
        DatabaseService.Table_roles[] mySQLRoles = mySql.sproc_GetAllRoles_but_user();
        assertEquals( 2, sqlServerRoles.length );
        assertEquals( 2, mySQLRoles.length );

        if( testMimer ) {
            DatabaseService.Table_roles[] mimerRoles = mimer.sproc_GetAllRoles_but_user();
            assertEquals( 2, mimerRoles.length );
            static_assertEquals( mimerRoles, sqlServerRoles, mySQLRoles );
        }
    }

    private void nonmodifyingTestSameResultFrom_sproc_getAllUsers() {
        DatabaseService.Table_users[] sqlServerUsers = sqlServer.sproc_GetAllUsers_OrderByLastName();
        DatabaseService.Table_users[] mySQLUsers = mySql.sproc_GetAllUsers_OrderByLastName();
        assertEquals( 2, sqlServerUsers.length );
        assertEquals( 2, mySQLUsers.length );

        if( testMimer ) {
            DatabaseService.Table_users[] mimerUsers = mimer.sproc_GetAllUsers_OrderByLastName();
            assertEquals( 2, mimerUsers.length );
            static_assertEquals( mimerUsers, sqlServerUsers, mySQLUsers );
        }
    }

    private void nonmodifyingTestSameResultFrom_sproc_getTemplatesInGroup() {
        DatabaseService.ViewTemplateGroup templateGroupZero = new DatabaseService.ViewTemplateGroup( 1, "Start" );

        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupZero = sqlServer.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, sqlServerTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, sqlServerTemplatesInGroupZero[0] );
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupOneo = sqlServer.sproc_GetTemplatesInGroup( 1 );
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupTwo = sqlServer.sproc_GetTemplatesInGroup( 2 );

        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupZero = mySql.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, mySQLTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, mySQLTemplatesInGroupZero[0] );
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupOne = mySql.sproc_GetTemplatesInGroup( 1 );
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupTwo = mySql.sproc_GetTemplatesInGroup( 2 );

        if( testMimer ) {
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupZero = mimer.sproc_GetTemplatesInGroup( 0 );
            assertEquals( 1, mimerTemplatesInGroupZero.length );
            assertEquals( templateGroupZero, mimerTemplatesInGroupZero[0] );
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupOne = mimer.sproc_GetTemplatesInGroup( 1 );
            assertEquals( mimerTemplatesInGroupOne.length, sqlServerTemplatesInGroupOneo.length );
            assertEquals( mimerTemplatesInGroupOne.length, mySQLTemplatesInGroupOne.length );
            DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupTwo = mimer.sproc_GetTemplatesInGroup( 2 );
            assertEquals( mimerTemplatesInGroupTwo.length, sqlServerTemplatesInGroupTwo.length );
            assertEquals( mimerTemplatesInGroupTwo.length, mySQLTemplatesInGroupTwo.length );
        }
    }

    private void nonmodifyingTest_sproc_getHighestUserId() {
        int sqlServerUserMax = sqlServer.sproc_getHighestUserId();
        assertEquals( 3, sqlServerUserMax );

        int mySqlUserMax = mySql.sproc_getHighestUserId();
        assertEquals( 3, mySqlUserMax );

        if( testMimer ) {
            int mimerUserMax = mimer.sproc_getHighestUserId();
            assertEquals( 3, mimerUserMax );
        }
    }

    // Modifiying tests below.

    public void test_sproc_AddNewuser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = static_createDummyUser( nextFreeUserId );

        static_test_sproc_AddNewuser( sqlServer, user );
        static_test_sproc_AddNewuser( mySql, user );
        if( testMimer ) {
            static_test_sproc_AddNewuser( mimer, user );
        }
    }

    private void static_test_sproc_AddNewuser( DatabaseService dbService, DatabaseService.Table_users user ) {
        DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
        dbService.sproc_AddNewuser( user );
        DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
        assertTrue( usersAfter.length == usersBefore.length + 1 );
    }

    public void test_sproc_updateUser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = static_createDummyUser( nextFreeUserId );

        test_sproc_updateUser( sqlServer, user, nextFreeUserId );
        test_sproc_updateUser( mySql, user, nextFreeUserId );
        if( testMimer ) {
            test_sproc_updateUser( mimer, user, nextFreeUserId );
        }
    }

    private void test_sproc_updateUser( DatabaseService dbService, DatabaseService.Table_users user, int nextFreeUserId ) {
        dbService.sproc_AddNewuser( user );
        int rowCount = dbService.sproc_updateUser( user );
        assertEquals( 1, rowCount );
        DatabaseService.Table_users modifiedUser = getUser( dbService, nextFreeUserId );
        assertEquals( user, modifiedUser );
    }

    public void test_sproc_delUser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = static_createDummyUser( nextFreeUserId );

        static_test_sproc_delUsers( mySql, user, nextFreeUserId );
        static_test_sproc_delUsers( sqlServer, user, nextFreeUserId );
        if( testMimer ) {
            static_test_sproc_delUsers( mimer, user, nextFreeUserId );
        }
    }

    private static void static_test_sproc_delUsers( DatabaseService dbService, DatabaseService.Table_users user, int nextFreeUserId ) {
        DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
        dbService.sproc_AddNewuser( user );
        int rowsAffected = dbService.sproc_delUser( nextFreeUserId );
        assertTrue( rowsAffected > 0 );
        DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
        static_assertEquals( usersBefore, usersAfter );
    }

    public void test_sproc_phoneNbrAdd() {
        DatabaseService.Table_users user = static_createDummyUser( 3 );
        static_test_sproc_phoneNbrAdd( sqlServer, user );
        static_test_sproc_phoneNbrAdd( mySql, user );
        if( testMimer ) {
            static_test_sproc_phoneNbrAdd( mimer, user );
        }
    }

    public void test_sproc_DelPhoneNr() {
        static_test_sproc_DelPhoneNr( sqlServer );
        static_test_sproc_DelPhoneNr( mySql );
        if( testMimer ) static_test_sproc_DelPhoneNr( sqlServer );
    }

    private void static_test_sproc_DelPhoneNr( DatabaseService dbService ) {
        int rowCount = dbService.sproc_DelPhoneNr( 2 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( 2, "9887655", 0 );
        dbService.sproc_phoneNbrAdd( 2, "123456", 1 );
        rowCount = dbService.sproc_DelPhoneNr( 2 );
        assertEquals( 2, rowCount );
    }

    private void static_test_sproc_phoneNbrAdd( DatabaseService dbService, DatabaseService.Table_users user ) {
        dbService.sproc_AddNewuser( user );
        int rowCount = dbService.sproc_phoneNbrAdd( 3, "1234567", 0 );
        assertEquals( 1, rowCount );
    }

    public void test_sproc_AddUseradminPermissibleRoles() {
        assertEquals( 1, sqlServer.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        assertEquals( 1, mySql.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        if( testMimer ) {
            assertEquals( 1, mimer.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        }
    }

    public void test_sproc_ChangeUserActiveStatus() {
        DatabaseService.Table_users user = static_createDummyUser( 3 );
        user.active = 1;

        static_test_sproc_ChangeUserActiveStatus( sqlServer, user );
        static_test_sproc_ChangeUserActiveStatus( mySql, user );
        if( testMimer ) {
            static_test_sproc_ChangeUserActiveStatus( mimer, user );
        }
    }

    private static void static_test_sproc_ChangeUserActiveStatus( DatabaseService dbService, DatabaseService.Table_users user ) {
        dbService.sproc_AddNewuser( user );

        dbService.sproc_ChangeUserActiveStatus( 3, false );
        DatabaseService.Table_users modifiedUser = getUser( dbService, 3 );
        assertEquals( 0 , modifiedUser.active );

        dbService.sproc_ChangeUserActiveStatus( 3, true );
        DatabaseService.Table_users modifiedUser2 = getUser( dbService, 3 );
        assertEquals( 1 , modifiedUser2.active );
    }

    public void test_sproc_AddUserRole() {
        DatabaseService.Table_user_roles_crossref existing = new DatabaseService.Table_user_roles_crossref( 1, 0 );
        DatabaseService.Table_user_roles_crossref nonExisting = new DatabaseService.Table_user_roles_crossref( 1, 1 );

        static_test_sproc_AddUserRole( sqlServer, existing, nonExisting );
        static_test_sproc_AddUserRole( mySql, existing, nonExisting );
        if( testMimer ) static_test_sproc_AddUserRole( mimer, existing, nonExisting );
    }

    private static void static_test_sproc_AddUserRole( DatabaseService dbService, DatabaseService.Table_user_roles_crossref existing, DatabaseService.Table_user_roles_crossref nonExisting ) {
        assertEquals( 0, dbService.sproc_AddUserRole( existing ) );
        assertEquals( 1, dbService.sproc_AddUserRole( nonExisting ) );
        assertEquals( 0, dbService.sproc_AddUserRole( nonExisting ) );
    }

    public void test_sproc_FindUserName() {
        static_test_sproc_FindUserName( sqlServer );
        static_test_sproc_FindUserName( mySql );
        if( testMimer ) static_test_sproc_FindUserName( mimer );
    }

    private void static_test_sproc_FindUserName( DatabaseService dbService ) {
        String name = "Admin";
        String nameResult = dbService.sproc_FindUserName(name);
        assertTrue( name.equalsIgnoreCase( nameResult ));
        name = "admin";
        nameResult = dbService.sproc_FindUserName(name);
        assertTrue( name.equalsIgnoreCase( nameResult ));
    }

    // Below is helper functions to more than one test.

    private static DatabaseService.Table_users static_createDummyUser( int nextFreeUserId ) {
        DatabaseService.Table_users user = new DatabaseService.Table_users( nextFreeUserId, "test login name", "test password", "First name", "Last name", "Titel", "Company", "Adress", "City", "Zip", "Country", "Country council", "Email adress", 0, 1001, 0, 1, 1, 1, new Timestamp( new java.util.Date().getTime() ) );
        return user;
    }

    // todo: this should be a method on Service?
    private static DatabaseService.Table_users getUser( DatabaseService dbService, int user_id ) {
        DatabaseService.Table_users[] sqlServerUsers = dbService.sproc_GetAllUsers_OrderByLastName();
        DatabaseService.Table_users modifiedUser = null;
        int i = 0;
        while( modifiedUser == null ) {
            if( sqlServerUsers[i].user_id == user_id ) {
                modifiedUser = sqlServerUsers[i];
            }
            i++;
        }
        return modifiedUser;
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

    private static DatabaseService static_initMimer() {
        DatabaseService dbService = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        return dbService;
    }

    private static DatabaseService static_initSqlServer() {
        DatabaseService dbService = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        return dbService;
    }

    private static DatabaseService static_initMySql() {
        DatabaseService dbService = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        return dbService;
    }

}
