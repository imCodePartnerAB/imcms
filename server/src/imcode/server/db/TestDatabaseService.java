package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;
import java.io.IOException;

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

    private final static int ADMIN_ID = 1;
    private final static int USER_ID = 2;
    private final static int TEST_DOC_ID_FIRST = 9001;
    private final static int TEST_DOC_ID_SECOND = 9002;
    private final static int TEST_DOC_ID_THIRD_FILE_DOC_TYPE = 9003;
    private final static int TEST_DOC_ID_DETACHED = 9999;


    private DatabaseService sqlServer;
    private DatabaseService mimer;
    private DatabaseService mySql;
    protected void setUp() throws IOException {
        mySql = static_initMySql();
        sqlServer = static_initSqlServer();
        mimer = static_initMimer();
    }

    public void test_sproc_getAllRoles() {
        assertEquals( 2, sqlServer.sproc_GetAllRoles_but_user().length );
        assertEquals( 2, mySql.sproc_GetAllRoles_but_user().length );

        assertEquals( 2, mimer.sproc_GetAllRoles_but_user().length );
        static_assertEquals( sqlServer.sproc_GetAllRoles_but_user(), mySql.sproc_GetAllRoles_but_user(), mimer.sproc_GetAllRoles_but_user() );
    }

    public void test_sproc_getAllUsers() {
        assertEquals( 2, sqlServer.sproc_GetAllUsers_OrderByLastName().length );
        assertEquals( 2, mySql.sproc_GetAllUsers_OrderByLastName().length );
        assertEquals( 2, mimer.sproc_GetAllUsers_OrderByLastName().length );
        static_assertEquals( mimer.sproc_GetAllUsers_OrderByLastName(), sqlServer.sproc_GetAllUsers_OrderByLastName(), mySql.sproc_GetAllUsers_OrderByLastName() );
    }

    public void test_sproc_getTemplatesInGroup() {
        DatabaseService.View_TemplateGroup templateGroupZero = new DatabaseService.View_TemplateGroup( 1, "Start" );

        DatabaseService.View_TemplateGroup[] sqlServerTemplatesInGroupZero = sqlServer.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, sqlServerTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, sqlServerTemplatesInGroupZero[0] );
        DatabaseService.View_TemplateGroup[] sqlServerTemplatesInGroupOneo = sqlServer.sproc_GetTemplatesInGroup( 1 );
        DatabaseService.View_TemplateGroup[] sqlServerTemplatesInGroupTwo = sqlServer.sproc_GetTemplatesInGroup( 2 );

        DatabaseService.View_TemplateGroup[] mySQLTemplatesInGroupZero = mySql.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, mySQLTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, mySQLTemplatesInGroupZero[0] );
        DatabaseService.View_TemplateGroup[] mySQLTemplatesInGroupOne = mySql.sproc_GetTemplatesInGroup( 1 );
        DatabaseService.View_TemplateGroup[] mySQLTemplatesInGroupTwo = mySql.sproc_GetTemplatesInGroup( 2 );

        DatabaseService.View_TemplateGroup[] mimerTemplatesInGroupZero = mimer.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, mimerTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, mimerTemplatesInGroupZero[0] );
        DatabaseService.View_TemplateGroup[] mimerTemplatesInGroupOne = mimer.sproc_GetTemplatesInGroup( 1 );
        DatabaseService.View_TemplateGroup[] mimerTemplatesInGroupTwo = mimer.sproc_GetTemplatesInGroup( 2 );

        assertEquals( mimerTemplatesInGroupOne.length, sqlServerTemplatesInGroupOneo.length );
        assertEquals( mimerTemplatesInGroupOne.length, mySQLTemplatesInGroupOne.length );
        assertEquals( mimerTemplatesInGroupTwo.length, sqlServerTemplatesInGroupTwo.length );
        assertEquals( mimerTemplatesInGroupTwo.length, mySQLTemplatesInGroupTwo.length );
    }

    public void test_sproc_getHighestUserId() {
        int sqlServerUserMax = sqlServer.sproc_getHighestUserId();
        assertEquals( 3, sqlServerUserMax );

        int mySqlUserMax = mySql.sproc_getHighestUserId();
        assertEquals( 3, mySqlUserMax );

        int mimerUserMax = mimer.sproc_getHighestUserId();
        assertEquals( 3, mimerUserMax );
    }

    public void test_sproc_AddNewuser() {
        int nextFreeUserId = 3;
        DatabaseService.Table_users user = static_createDummyUser( nextFreeUserId );

        static_test_sproc_AddNewuser( sqlServer, user );
        static_test_sproc_AddNewuser( mySql, user );
        static_test_sproc_AddNewuser( mimer, user );
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
        test_sproc_updateUser( mimer, user, nextFreeUserId );
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
        static_test_sproc_delUsers( mimer, user, nextFreeUserId );
    }

    private static void static_test_sproc_delUsers( DatabaseService dbService, DatabaseService.Table_users user, int nextFreeUserId ) {
        DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
        dbService.sproc_AddNewuser( user );
        int rowsAffected = dbService.sproc_delUser( nextFreeUserId );
        assertTrue( rowsAffected > 0 );
        DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
        static_assertEquals( usersBefore, usersAfter );
    }

    public void test_sproc_GetPhonetypeName() {
        assertEquals( "Annat", sqlServer.sproc_GetPhonetypeName( 0, 1 ) );
        assertEquals( "Annat", mySql.sproc_GetPhonetypeName( 0, 1 ) );
        assertEquals( "Annat", mimer.sproc_GetPhonetypeName( 0, 1 ) );
    }

    public void test_sproc_GetPhonetypes_ORDER_BY_phonetype_id() {
        assertEquals( 5, sqlServer.sproc_GetPhonetypes_ORDER_BY_phonetype_id( 1 ).length );
        assertEquals( 5, mySql.sproc_GetPhonetypes_ORDER_BY_phonetype_id( 1 ).length );
        assertEquals( 5, mimer.sproc_GetPhonetypes_ORDER_BY_phonetype_id( 1 ).length );
    }

    public void test_sproc_GetUserPhoneNumbers() {
        test_sproc_GetUserPhoneNumbers( sqlServer );
        test_sproc_GetUserPhoneNumbers( mySql );
        test_sproc_GetUserPhoneNumbers( mimer );
    }

    private void test_sproc_GetUserPhoneNumbers( DatabaseService dbService ) {
        int user_id = 2;
        assertEquals( 0, dbService.sproc_GetUserPhoneNumbers( user_id ).length );
        dbService.sproc_phoneNbrAdd( user_id, "345345-34534", 0 );
        assertEquals( 1, dbService.sproc_GetUserPhoneNumbers( user_id ).length );
    }

    public void test_sproc_GetUserPhones() {
        test_sproc_GetUserPhones( sqlServer );
        test_sproc_GetUserPhones( mySql );
        test_sproc_GetUserPhones( mimer );
    }

    private void test_sproc_GetUserPhones( DatabaseService dbService ) {
        int user_id = 2;
        assertEquals( 0, dbService.sproc_GetUserPhones( user_id ).length );
        dbService.sproc_phoneNbrAdd( user_id, "345345-34534", 0 );
        assertEquals( 1, dbService.sproc_GetUserPhones( user_id ).length );
    }

    public void test_sproc_phoneNbrAdd() {
        DatabaseService.Table_users user = static_createDummyUser( 3 );
        static_test_sproc_phoneNbrAdd( sqlServer, user );
        static_test_sproc_phoneNbrAdd( mySql, user );
        {
            static_test_sproc_phoneNbrAdd( mimer, user );
        }
    }

    public void test_sproc_PhoneNbrUpdate() {
        static_test_sproc_PhoneNbrUpdate( sqlServer );
        static_test_sproc_PhoneNbrUpdate( mySql );
        static_test_sproc_PhoneNbrUpdate( mimer );
    }

    private void static_test_sproc_PhoneNbrUpdate( DatabaseService dbService ) {
        int rowCount = dbService.sproc_PhoneNbrUpdate( 2, 1, "666666", 1 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( 2, "034985", 0 );
        rowCount = dbService.sproc_PhoneNbrUpdate( 2, 1, "666666", 1 );
        assertEquals( 1, rowCount );
    }

    public void test_sproc_DelPhoneNr() {
        static_test_sproc_DelPhoneNr( sqlServer );
        static_test_sproc_DelPhoneNr( mySql );
        static_test_sproc_DelPhoneNr( sqlServer );
    }

    private void static_test_sproc_DelPhoneNr( DatabaseService dbService ) {
        int rowCount = dbService.sproc_DelPhoneNr( 2 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( 2, "9887655", 0 );
        dbService.sproc_phoneNbrAdd( 2, "123456", 1 );
        rowCount = dbService.sproc_DelPhoneNr( 2 );
        assertEquals( 2, rowCount );
    }

    public void test_sproc_PhoneNbrDelete() {
        static_test_sproc_PhoneNbrDelete( sqlServer );
        static_test_sproc_PhoneNbrDelete( mySql );
        static_test_sproc_PhoneNbrDelete( mimer );
    }

    private void static_test_sproc_PhoneNbrDelete( DatabaseService dbService ) {
        int rowCount = dbService.sproc_PhoneNbrDelete( 1 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( 2, "9887655", 0 );
        dbService.sproc_phoneNbrAdd( 2, "123456", 1 );
        rowCount = dbService.sproc_PhoneNbrDelete( 1 );
        rowCount += dbService.sproc_PhoneNbrDelete( 2 );
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
        {
            assertEquals( 1, mimer.sproc_AddUseradminPermissibleRoles( 1, 2 ) );
        }
    }

    public void test_sproc_ChangeUserActiveStatus() {
        DatabaseService.Table_users user = static_createDummyUser( 3 );
        user.active = 1;

        static_test_sproc_ChangeUserActiveStatus( sqlServer, user );
        static_test_sproc_ChangeUserActiveStatus( mySql, user );
        {
            static_test_sproc_ChangeUserActiveStatus( mimer, user );
        }
    }

    private static void static_test_sproc_ChangeUserActiveStatus( DatabaseService dbService, DatabaseService.Table_users user ) {
        dbService.sproc_AddNewuser( user );

        dbService.sproc_ChangeUserActiveStatus( 3, false );
        DatabaseService.Table_users modifiedUser = getUser( dbService, 3 );
        assertEquals( 0, modifiedUser.active );

        dbService.sproc_ChangeUserActiveStatus( 3, true );
        DatabaseService.Table_users modifiedUser2 = getUser( dbService, 3 );
        assertEquals( 1, modifiedUser2.active );
    }

    public void test_sproc_AddUserRole() {
        DatabaseService.Table_user_roles_crossref existing = new DatabaseService.Table_user_roles_crossref( 1, 0 );
        DatabaseService.Table_user_roles_crossref nonExisting = new DatabaseService.Table_user_roles_crossref( 1, 1 );

        static_test_sproc_AddUserRole( sqlServer, existing, nonExisting );
        static_test_sproc_AddUserRole( mySql, existing, nonExisting );
        static_test_sproc_AddUserRole( mimer, existing, nonExisting );
    }

    private static void static_test_sproc_AddUserRole( DatabaseService dbService, DatabaseService.Table_user_roles_crossref existing, DatabaseService.Table_user_roles_crossref nonExisting ) {
        assertEquals( 0, dbService.sproc_AddUserRole( existing ) );
        assertEquals( 1, dbService.sproc_AddUserRole( nonExisting ) );
        assertEquals( 0, dbService.sproc_AddUserRole( nonExisting ) );
    }

    public void test_sproc_FindUserName() {
        assertTrue( sqlServer.sproc_FindUserName( "Admin" ) );
        assertTrue( sqlServer.sproc_FindUserName( "admin" ) );

        assertTrue( mySql.sproc_FindUserName( "Admin" ) );
        assertTrue( mySql.sproc_FindUserName( "admin" ) );

        assertTrue( mimer.sproc_FindUserName( "Admin" ) );
        assertTrue( mimer.sproc_FindUserName( "admin" ) );
    }

    public void test_sproc_DocumentDelete() {
        assertEquals( 5, sqlServer.sproc_DocumentDelete(1001) );
        assertEquals( 5, mySql.sproc_DocumentDelete(1001) );
        assertEquals( 5, mimer.sproc_DocumentDelete(1001) );
    }

    public void test_sproc_FindMetaId() {
        assertTrue( sqlServer.sproc_FindMetaId(1001) );
        assertFalse( sqlServer.sproc_FindMetaId(66666) );

        assertTrue( mySql.sproc_FindMetaId(1001) );
        assertFalse( mySql.sproc_FindMetaId(66666) );

        assertTrue( mimer.sproc_FindMetaId(1001) );
        assertFalse( mimer.sproc_FindMetaId(66666) );
    }

    public void test_sproc_getDocs() {
        assertEquals( 4, sqlServer.sproc_getDocs(1, 1, 10000).length );
        assertEquals( 4, mySql.sproc_getDocs(1, 1, 10000).length );
        assertEquals( 4, mimer.sproc_getDocs(1, 1, 10000).length );
    }

    public void test_sproc_CheckForFileDocs() {
        test_sproc_CheckForFileDocs( sqlServer );
        test_sproc_CheckForFileDocs( mySql );
        test_sproc_CheckForFileDocs( mimer );
    }

    private void test_sproc_CheckForFileDocs( DatabaseService dbService ) {
        int[] documentIds = new int[]{ TEST_DOC_ID_FIRST, TEST_DOC_ID_SECOND, TEST_DOC_ID_THIRD_FILE_DOC_TYPE, TEST_DOC_ID_DETACHED };
        int[] fileDocumentIds = dbService.sproc_CheckForFileDocs( documentIds );
        assertEquals( 1, fileDocumentIds.length );
        assertEquals( TEST_DOC_ID_THIRD_FILE_DOC_TYPE,  fileDocumentIds[0] );
    }

    public void test_sproc_getChilds() {
        test_sproc_getChilds( sqlServer );
        test_sproc_getChilds( mySql );
        test_sproc_getChilds( mimer );
    }

    private void test_sproc_getChilds( DatabaseService dbService ) {
        DatabaseService.View_ChildData[] children = dbService.sproc_getChilds( TEST_DOC_ID_DETACHED, ADMIN_ID );
        assertEquals( 0, children.length );

        children = dbService.sproc_getChilds( TEST_DOC_ID_FIRST, ADMIN_ID );
        assertEquals( 1, children.length );
    }

    public void test_sproc_AddExistingDocToMenu() {
        test_sproc_AddExistingDocToMenu( sqlServer );
        test_sproc_AddExistingDocToMenu( mySql );
        test_sproc_AddExistingDocToMenu( mimer );
    }

    private void test_sproc_AddExistingDocToMenu( DatabaseService dbService ) {
        int linksBefore = dbService.sproc_getChilds( TEST_DOC_ID_DETACHED, ADMIN_ID ).length;

        int rowCount = dbService.sproc_AddExistingDocToMenu(TEST_DOC_ID_DETACHED, TEST_DOC_ID_DETACHED, 1 );
        assertEquals( 1, rowCount );

        int linksAfter = dbService.sproc_getChilds( TEST_DOC_ID_DETACHED, ADMIN_ID ).length;
        assertEquals( linksBefore + 1 , linksAfter );
    }

    public void test_sproc_CheckAdminRights() {
        assertTrue( sqlServer.sproc_CheckAdminRights( ADMIN_ID ));
        assertFalse( sqlServer.sproc_CheckAdminRights( USER_ID ));

        assertTrue( mySql.sproc_CheckAdminRights( ADMIN_ID ));
        assertFalse( mySql.sproc_CheckAdminRights( USER_ID ));

        assertTrue( mimer.sproc_CheckAdminRights( ADMIN_ID ));
        assertFalse( mimer.sproc_CheckAdminRights( USER_ID ));
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

    private static DatabaseService static_initMimer() throws IOException {
        DatabaseService dbService = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        dbService.initTestData();
        return dbService;
    }

    private static DatabaseService static_initSqlServer() throws IOException {
        DatabaseService dbService = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        dbService.initTestData();
        return dbService;
    }

    private static DatabaseService static_initMySql() throws IOException {
        DatabaseService dbService = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        dbService.initializeDatabase();
        dbService.initTestData();
        return dbService;
    }
}
