package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Clob;
import java.io.IOException;

public class TestDatabaseService extends Log4JConfiguredTestCase {

    // You may have to change these for your local installation of the databases
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

    // If you change anything in the scripts that creates the database
    // you propably have to change some of the following constants.
    private final static int USER_ADMIN_ID = 1;
    private final static int USER_USER_ID = 2;
    public static final int USER_TEST_ID = 3;
    private static final int USER_NEXT_FREE_ID = 4;
    private static final int USER_ID_NON_EXISTING = 2452345;

    private static int ROLE_SUPER_ADMIN_ID = 0;
    private static int ROLE_USER_ADMIN_ID = 1;
    public static final int ROLE_TEST_ID = 3;
    private final static int ROLE_NEXT_FREE_ID = 4;

    private int DOC_NO_OF_DOCS = 5; // 1001 + folowing
    private static final int DOC_ID_FIRST_PAGE = 1001;
    private static final int DOC_ID_NON_EXISTING = 66666;
    private final static int DOC_FIRST_TEST_ID = 9001;
    private final static int DOC_TEST_SECOND_ID = 9002;
    private final static int DOC_TEST_THIRD_ID_FILE_DOC_TYPE = 9003;
    private final static int DOC_TEST_ID_DETACHED = 9999;

    private int NEXT_FREE_PHONE_ID = 1;
    private static final int PHONE_TYPE_HOME = 1;
    private static final int PHONE_TYPE_OTHER = 0;

    private static final int LANG_ID_SWEDEN = 1;

    private DatabaseService sqlServer;
    private DatabaseService mySql;
    private DatabaseService mimer;

    protected void setUp() throws IOException {
        mySql = static_initMySql();
        sqlServer = static_initSqlServer();
        mimer = static_initMimer();
    }

    public void test_sproc_getAllRoles() {
        int noOfRoles = ROLE_NEXT_FREE_ID - 1;
        assertEquals( noOfRoles, sqlServer.sproc_GetAllRoles_but_user().length );
        assertEquals( noOfRoles, mySql.sproc_GetAllRoles_but_user().length );

        if( null != mimer ) {
            assertEquals( noOfRoles, mimer.sproc_GetAllRoles_but_user().length );
            static_assertEquals( sqlServer.sproc_GetAllRoles_but_user(), mySql.sproc_GetAllRoles_but_user(), mimer.sproc_GetAllRoles_but_user() );
        }
    }

    public void test_sproc_getAllUsers() {
        int noOfUsers = USER_NEXT_FREE_ID - 1;
        assertEquals( noOfUsers, sqlServer.sproc_GetAllUsers_OrderByLastName().length );
        assertEquals( noOfUsers, mySql.sproc_GetAllUsers_OrderByLastName().length );
        if( null != mimer )
            assertEquals( noOfUsers, mimer.sproc_GetAllUsers_OrderByLastName().length );
        if( null != mimer )
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

        if( null != mimer ) {
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
    }

    public void test_sproc_getHighestUserId() {
        int highestUserId = USER_NEXT_FREE_ID - 1;

        assertEquals( highestUserId, sqlServer.sproc_getHighestUserId() );
        assertEquals( highestUserId, mySql.sproc_getHighestUserId() );
        if( null != mimer )
            assertEquals( highestUserId, mimer.sproc_getHighestUserId() );
    }

    public void test_sproc_AddNewuser() {
        DatabaseService.Table_users user = static_createDummyUser();

        static_test_sproc_AddNewuser( sqlServer, user );
        static_test_sproc_AddNewuser( mySql, user );
        if( null != mimer )
            static_test_sproc_AddNewuser( mimer, user );
    }

    private void static_test_sproc_AddNewuser( DatabaseService dbService, DatabaseService.Table_users user ) {
        DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
        dbService.sproc_AddNewuser( user );
        DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
        assertTrue( usersAfter.length == usersBefore.length + 1 );
    }

    public void test_sproc_updateUser() {
        DatabaseService.Table_users user = static_createDummyUser();
        test_sproc_updateUser( sqlServer, user, USER_NEXT_FREE_ID );
        test_sproc_updateUser( mySql, user, USER_NEXT_FREE_ID );
        if( null != mimer )
            test_sproc_updateUser( mimer, user, USER_NEXT_FREE_ID );
    }

    private void test_sproc_updateUser( DatabaseService dbService, DatabaseService.Table_users user, int nextFreeUserId ) {
        dbService.sproc_AddNewuser( user );
        int rowCount = dbService.sproc_updateUser( user );
        assertEquals( 1, rowCount );
        DatabaseService.Table_users modifiedUser = getUser( dbService, nextFreeUserId );
        assertEquals( user, modifiedUser );
    }

    public void test_sproc_delUser() {
        DatabaseService.Table_users user = static_createDummyUser();

        static_test_sproc_delUsers( mySql, user, USER_NEXT_FREE_ID );
        static_test_sproc_delUsers( sqlServer, user, USER_NEXT_FREE_ID );
        if( null != mimer )
            static_test_sproc_delUsers( mimer, user, USER_NEXT_FREE_ID );
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
        if( null != mimer )
            assertEquals( "Annat", mimer.sproc_GetPhonetypeName( 0, 1 ) );
    }

    public void test_sproc_GetPhonetypes_ORDER_BY_phonetype_id() {
        assertEquals( 5, sqlServer.sproc_GetPhonetypes_ORDER_BY_phonetype_id( LANG_ID_SWEDEN ).length );
        assertEquals( 5, mySql.sproc_GetPhonetypes_ORDER_BY_phonetype_id( LANG_ID_SWEDEN ).length );
        if( null != mimer )
            assertEquals( 5, mimer.sproc_GetPhonetypes_ORDER_BY_phonetype_id( LANG_ID_SWEDEN ).length );
    }

    public void test_sproc_GetUserPhoneNumbers() {
        test_sproc_GetUserPhoneNumbers( sqlServer );
        test_sproc_GetUserPhoneNumbers( mySql );
        if( null != mimer )
            test_sproc_GetUserPhoneNumbers( mimer );
    }

    private void test_sproc_GetUserPhoneNumbers( DatabaseService dbService ) {
        assertEquals( 0, dbService.sproc_GetUserPhoneNumbers( USER_USER_ID ).length );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "345345-34534", 0 );
        assertEquals( 1, dbService.sproc_GetUserPhoneNumbers( USER_USER_ID ).length );
    }

    public void test_sproc_GetUserPhones() {
        test_sproc_GetUserPhones( sqlServer );
        test_sproc_GetUserPhones( mySql );
        if( null != mimer )
            test_sproc_GetUserPhones( mimer );
    }

    private void test_sproc_GetUserPhones( DatabaseService dbService ) {
        int user_id = 2;
        assertEquals( 0, dbService.sproc_GetUserPhones( user_id ).length );
        dbService.sproc_phoneNbrAdd( user_id, "345345-34534", 0 );
        assertEquals( 1, dbService.sproc_GetUserPhones( user_id ).length );
    }

    public void test_sproc_phoneNbrAdd() {
        DatabaseService.Table_users user = static_createDummyUser();
        static_test_sproc_phoneNbrAdd( sqlServer, user );
        static_test_sproc_phoneNbrAdd( mySql, user );
        if( null != mimer )
            static_test_sproc_phoneNbrAdd( mimer, user );
    }

    public void test_sproc_PhoneNbrUpdate() {
        static_test_sproc_PhoneNbrUpdate( sqlServer );
        static_test_sproc_PhoneNbrUpdate( mySql );
        if( null != mimer )
            static_test_sproc_PhoneNbrUpdate( mimer );
    }

    private void static_test_sproc_PhoneNbrUpdate( DatabaseService dbService ) {
        int rowCount = dbService.sproc_PhoneNbrUpdate( USER_USER_ID, NEXT_FREE_PHONE_ID, "666666", PHONE_TYPE_HOME );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "034985", PHONE_TYPE_OTHER );
        rowCount = dbService.sproc_PhoneNbrUpdate( USER_USER_ID, NEXT_FREE_PHONE_ID, "666666", PHONE_TYPE_HOME );
        assertEquals( 1, rowCount );
    }

    public void test_sproc_DelPhoneNr() {
        static_test_sproc_DelPhoneNr( sqlServer );
        static_test_sproc_DelPhoneNr( mySql );
        if( null != mimer )
            static_test_sproc_DelPhoneNr( mimer );
    }

    private void static_test_sproc_DelPhoneNr( DatabaseService dbService ) {
        int rowCount = dbService.sproc_DelPhoneNr( 2 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "9887655",  PHONE_TYPE_OTHER );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "123456", PHONE_TYPE_HOME );
        rowCount = dbService.sproc_DelPhoneNr( USER_USER_ID );
        assertEquals( 2, rowCount );
    }

    public void test_sproc_PhoneNbrDelete() {
        static_test_sproc_PhoneNbrDelete( sqlServer );
        static_test_sproc_PhoneNbrDelete( mySql );
        if( null != mimer )
            static_test_sproc_PhoneNbrDelete( mimer );
    }

    private void static_test_sproc_PhoneNbrDelete( DatabaseService dbService ) {
        int rowCount = dbService.sproc_PhoneNbrDelete( 1 );
        assertEquals( 0, rowCount );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "9887655", 0 );
        dbService.sproc_phoneNbrAdd( USER_USER_ID, "123456", 1 );
        rowCount = dbService.sproc_PhoneNbrDelete( NEXT_FREE_PHONE_ID );
        rowCount += dbService.sproc_PhoneNbrDelete( NEXT_FREE_PHONE_ID + 1 );
        assertEquals( 2, rowCount );
    }

    private void static_test_sproc_phoneNbrAdd( DatabaseService dbService, DatabaseService.Table_users user ) {
        dbService.sproc_AddNewuser( user );
        int rowCount = dbService.sproc_phoneNbrAdd( USER_TEST_ID, "1234567", PHONE_TYPE_OTHER );
        assertEquals( 1, rowCount );
    }

    public void test_sproc_AddUseradminPermissibleRoles() {
        assertEquals( 1, sqlServer.sproc_AddUseradminPermissibleRoles( USER_TEST_ID, ROLE_TEST_ID ) );
        assertEquals( 1, mySql.sproc_AddUseradminPermissibleRoles( USER_TEST_ID, ROLE_TEST_ID ) );
        if( null != mimer )
            assertEquals( 1, mimer.sproc_AddUseradminPermissibleRoles( USER_TEST_ID, ROLE_TEST_ID ) );
    }

    public void test_sproc_ChangeUserActiveStatus() {
        DatabaseService.Table_users user = static_createDummyUser();
        user.active = 1;

        static_test_sproc_ChangeUserActiveStatus( sqlServer, user );
        static_test_sproc_ChangeUserActiveStatus( mySql, user );
        if( null != mimer )
            static_test_sproc_ChangeUserActiveStatus( mimer, user );
    }

    private static void static_test_sproc_ChangeUserActiveStatus( DatabaseService dbService, DatabaseService.Table_users user ) {
        dbService.sproc_AddNewuser( user );

        dbService.sproc_ChangeUserActiveStatus( USER_TEST_ID, false );
        DatabaseService.Table_users modifiedUser = getUser( dbService, USER_TEST_ID );
        assertEquals( 0, modifiedUser.active );

        dbService.sproc_ChangeUserActiveStatus( USER_TEST_ID, true );
        DatabaseService.Table_users modifiedUser2 = getUser( dbService, USER_TEST_ID );
        assertEquals( 1, modifiedUser2.active );
    }

    public void test_sproc_AddUserRole() {
        static_test_sproc_AddUserRole( sqlServer );
        static_test_sproc_AddUserRole( mySql );
        if( null != mimer )
            static_test_sproc_AddUserRole( mimer );
    }

    private static void static_test_sproc_AddUserRole( DatabaseService dbService ) {
        assertEquals( 0, dbService.sproc_AddUserRole( USER_ADMIN_ID, ROLE_SUPER_ADMIN_ID ) );
        assertEquals( 1, dbService.sproc_AddUserRole( USER_TEST_ID, ROLE_USER_ADMIN_ID ) );
        assertEquals( 0, dbService.sproc_AddUserRole( USER_TEST_ID, ROLE_USER_ADMIN_ID ) );
    }

    public void test_sproc_FindUserName() {
        assertTrue( sqlServer.sproc_FindUserName( "Admin" ) );
        assertTrue( sqlServer.sproc_FindUserName( "admin" ) );

        assertTrue( mySql.sproc_FindUserName( "Admin" ) );
        assertTrue( mySql.sproc_FindUserName( "admin" ) );

        if( null != mimer ) {
            assertTrue( mimer.sproc_FindUserName( "Admin" ) );
            assertTrue( mimer.sproc_FindUserName( "admin" ) );
        }
    }

    public void test_sproc_DocumentDelete() {
        assertEquals( 5, sqlServer.sproc_DocumentDelete( DOC_ID_FIRST_PAGE ) );
        assertEquals( 5, mySql.sproc_DocumentDelete( DOC_ID_FIRST_PAGE ) );
        if( null != mimer )
            assertEquals( 5, mimer.sproc_DocumentDelete( DOC_ID_FIRST_PAGE ) );
    }

    public void test_sproc_FindMetaId() {
        assertTrue( sqlServer.sproc_FindMetaId( DOC_ID_FIRST_PAGE ) );
        assertFalse( sqlServer.sproc_FindMetaId( DOC_ID_NON_EXISTING ) );

        assertTrue( mySql.sproc_FindMetaId( DOC_ID_FIRST_PAGE ) );
        assertFalse( mySql.sproc_FindMetaId( DOC_ID_NON_EXISTING ) );

        if( null != mimer ) {
            assertTrue( mimer.sproc_FindMetaId( DOC_ID_FIRST_PAGE ) );
            assertFalse( mimer.sproc_FindMetaId( DOC_ID_NON_EXISTING ) );
        }
    }

    public void test_sproc_getDocs() {
        assertEquals( DOC_NO_OF_DOCS, sqlServer.sproc_getDocs( 1, 1, 100000 ).length );
        assertEquals( DOC_NO_OF_DOCS, mySql.sproc_getDocs( 1, 1, 100000 ).length );
        if( null != mimer )
            assertEquals( DOC_NO_OF_DOCS, mimer.sproc_getDocs( 1, 1, 100000 ).length );
    }

    public void test_sproc_CheckForFileDocs() {
        test_sproc_CheckForFileDocs( sqlServer );
        test_sproc_CheckForFileDocs( mySql );
        if( null != mimer )
            test_sproc_CheckForFileDocs( mimer );
    }

    private void test_sproc_CheckForFileDocs( DatabaseService dbService ) {
        int[] documentIds = new int[]{DOC_FIRST_TEST_ID, DOC_TEST_SECOND_ID, DOC_TEST_THIRD_ID_FILE_DOC_TYPE, DOC_TEST_ID_DETACHED};
        int[] fileDocumentIds = dbService.sproc_CheckForFileDocs( documentIds );
        assertEquals( 1, fileDocumentIds.length );
        assertEquals( DOC_TEST_THIRD_ID_FILE_DOC_TYPE, fileDocumentIds[0] );
    }

    public void test_sproc_getChilds() {
        test_sproc_getChilds( sqlServer );
        test_sproc_getChilds( mySql );
        if( null != mimer )
            test_sproc_getChilds( mimer );
    }

    private void test_sproc_getChilds( DatabaseService dbService ) {
        DatabaseService.View_ChildData[] children = dbService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID );
        assertEquals( 0, children.length );

        children = dbService.sproc_getChilds( DOC_FIRST_TEST_ID, USER_ADMIN_ID );
        assertEquals( 1, children.length );
    }

    public void test_sproc_AddExistingDocToMenu() {
        test_sproc_AddExistingDocToMenu( sqlServer );
        test_sproc_AddExistingDocToMenu( mySql );
        if( null != mimer )
            test_sproc_AddExistingDocToMenu( mimer );
    }

    private void test_sproc_AddExistingDocToMenu( DatabaseService dbService ) {
        int linksBefore = dbService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID ).length;

        int doc_menu_no = 1;
        int rowCount = dbService.sproc_AddExistingDocToMenu( DOC_TEST_ID_DETACHED, DOC_TEST_ID_DETACHED, doc_menu_no );
        assertEquals( 1, rowCount );

        int linksAfter = dbService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID ).length;
        assertEquals( linksBefore + 1, linksAfter );
    }

    public void test_sproc_CheckAdminRights() {
        assertTrue( sqlServer.sproc_CheckAdminRights( USER_ADMIN_ID ) );
        assertFalse( sqlServer.sproc_CheckAdminRights( USER_USER_ID ) );

        assertTrue( mySql.sproc_CheckAdminRights( USER_ADMIN_ID ) );
        assertFalse( mySql.sproc_CheckAdminRights( USER_USER_ID ) );

        if( null != mimer ) {
            assertTrue( mimer.sproc_CheckAdminRights( USER_ADMIN_ID ) );
            assertFalse( mimer.sproc_CheckAdminRights( USER_USER_ID ) );
        }
    }

    public void test_sproc_CheckUserDocSharePermission() {
        assertTrue( sqlServer.sproc_CheckUserDocSharePermission( USER_ADMIN_ID, DOC_FIRST_TEST_ID ) );
        assertFalse( sqlServer.sproc_CheckUserDocSharePermission( USER_USER_ID, DOC_FIRST_TEST_ID ) );

        assertTrue( mySql.sproc_CheckUserDocSharePermission( USER_ADMIN_ID, DOC_FIRST_TEST_ID ) );
        assertFalse( mySql.sproc_CheckUserDocSharePermission( USER_USER_ID, DOC_FIRST_TEST_ID ) );

        if( null != mimer ) {
            assertTrue( mimer.sproc_CheckUserDocSharePermission( USER_ADMIN_ID, DOC_FIRST_TEST_ID ) );
            assertFalse( mimer.sproc_CheckUserDocSharePermission( USER_USER_ID, DOC_FIRST_TEST_ID ) );
        }
    }

    public void test_sproc_checkUserAdminrole() {
        test_sproc_checkUserAdminrole( sqlServer );
        test_sproc_checkUserAdminrole( mySql );
        if( null != mimer )
            test_sproc_checkUserAdminrole( mimer );
    }

    private void test_sproc_checkUserAdminrole( DatabaseService dbService ) {
        assertFalse( dbService.sproc_checkUserAdminrole( USER_USER_ID, 2 ) );
        assertTrue( dbService.sproc_checkUserAdminrole( USER_ADMIN_ID, 1 ) );
        assertFalse( dbService.sproc_checkUserAdminrole( USER_ID_NON_EXISTING, 2 ) );
    }

    // Below is helper functions to more than one test.

    private static DatabaseService.Table_users static_createDummyUser() {
        DatabaseService.Table_users user = new DatabaseService.Table_users( USER_NEXT_FREE_ID, "test login name", "test password", "First name", "Last name", "Titel", "Company", "Adress", "City", "Zip", "Country", "Country council", "Email adress", 0, DOC_ID_FIRST_PAGE, 0, 1, 1, 1, new Timestamp( new java.util.Date().getTime() ) );
        return user;
    }

    public void test_deleteUserRole() {
        test_deleteUserRole( sqlServer );
        test_deleteUserRole( mySql );
        if( null != mimer )
            test_deleteUserRole( mimer );
    }

    private void test_deleteUserRole( DatabaseService dbService ) {
        assertEquals( 0, dbService.sproc_DelUserRoles( USER_NEXT_FREE_ID, ROLE_NEXT_FREE_ID ) );
        assertEquals( 1, dbService.sproc_AddUserRole( USER_TEST_ID, ROLE_TEST_ID ) );
        assertEquals( 1, dbService.sproc_DelUserRoles( USER_TEST_ID, ROLE_TEST_ID ) );
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
