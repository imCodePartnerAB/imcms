package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;
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

    DatabaseService[] databaseServices = null;

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
    private static final int DOC_TYPE_TEXT_ID = 2;

    private static final String DOC_THIRD_DOC_FILENAME = "testfilename.txt";

    private int NEXT_FREE_PHONE_ID = 2;
    private static final int PHONE_TYPE_HOME = 1;
    private static final int PHONE_TYPE_OTHER = 0;

    private static final int LANG_ID_SWEDEN = 1;
    private static final int DOC_TYPE_FILE_ID = 8;

    protected void setUp() throws IOException {
        databaseServices = new DatabaseService[]{
            static_initMySql(),
            static_initSqlServer(),
            static_initMimer(),
        };
    }

    /**
     * I do all of them together for performance reasons.
     */
    public void testNonModfyingTests() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            test_sproc_getAllRoles( dbService );
            test_sproc_getAllUsers( dbService );
            test_sproc_getTemplatesInGroup( dbService );
            test_sproc_getHighestUserId( dbService );
            test_sproc_GetPhonetypeName( dbService );
            test_sproc_GetPhonetypes_ORDER_BY_phonetype_id( dbService );
            test_sproc_GetUserPhoneNumbers( dbService );
            test_sproc_GetUserPhones( dbService );
            test_sproc_FindUserName(  dbService );
            test_sproc_FindMetaId( dbService );
            test_sproc_getChilds( dbService );
            test_sproc_CheckForFileDocs( dbService );
            test_sproc_getDocs( dbService );
            test_sproc_CheckAdminRights( dbService );
            test_sproc_CheckUserDocSharePermission( dbService );
            test_sproc_checkUserAdminrole( dbService );
            test_sproc_GetFileName( dbService );
            test_sproc_GetDocType( dbService );
        }
    }

    private int test_sproc_getAllRoles( DatabaseService dbService ) {
        int noOfRoles = ROLE_NEXT_FREE_ID - 1;
        assertEquals( noOfRoles, dbService.sproc_GetAllRoles_but_user().length );
        return noOfRoles;
    }

    private void test_sproc_getAllUsers( DatabaseService dbService ) {
        int noOfUsers = USER_NEXT_FREE_ID - 1;
        assertEquals( noOfUsers, dbService.sproc_GetAllUsers_OrderByLastName().length );
    }

    private void test_sproc_getTemplatesInGroup( DatabaseService dbService ) {
        DatabaseService.View_TemplateGroup templateGroupZero = new DatabaseService.View_TemplateGroup( 1, "Start" );
        DatabaseService.View_TemplateGroup[] sqlServerTemplatesInGroupZero = dbService.sproc_GetTemplatesInGroup( 0 );
        assertEquals( 1, sqlServerTemplatesInGroupZero.length );
        assertEquals( templateGroupZero, sqlServerTemplatesInGroupZero[0] );
    }

    private void test_sproc_getHighestUserId( DatabaseService dbService ) {
        int highestUserId = USER_NEXT_FREE_ID - 1;
        assertEquals( highestUserId, dbService.sproc_getHighestUserId() );
    }

    private void test_sproc_GetPhonetypeName( DatabaseService dbService ) {
        assertEquals( "Annat", dbService.sproc_GetPhonetypeName( 0, 1 ) );
    }

    private void test_sproc_GetPhonetypes_ORDER_BY_phonetype_id( DatabaseService dbService ) {
        assertEquals( 5, dbService.sproc_GetPhonetypes_ORDER_BY_phonetype_id( LANG_ID_SWEDEN ).length );
    }

    private void test_sproc_GetUserPhoneNumbers( DatabaseService dbService ) {
        assertEquals( 0, dbService.sproc_GetUserPhoneNumbers( USER_USER_ID ).length );
        assertEquals( 1, dbService.sproc_GetUserPhoneNumbers( USER_TEST_ID ).length );
    }

    private void test_sproc_GetUserPhones( DatabaseService dbService ) {
        assertEquals( 0, dbService.sproc_GetUserPhones( USER_USER_ID ).length );
        assertEquals( 1, dbService.sproc_GetUserPhones( USER_TEST_ID ).length );
    }

    private void test_sproc_FindUserName( DatabaseService dbService ) {
        assertTrue( dbService.sproc_FindUserName( "Admin" ) );
        assertTrue( dbService.sproc_FindUserName( "admin" ) );
    }

    private void test_sproc_FindMetaId( DatabaseService dbService ) {
        assertTrue( dbService.sproc_FindMetaId( DOC_ID_FIRST_PAGE ) );
        assertFalse( dbService.sproc_FindMetaId( DOC_ID_NON_EXISTING ) );
    }


    private void test_sproc_getDocs( DatabaseService dbService ) {
        assertEquals( DOC_NO_OF_DOCS, dbService.sproc_getDocs( USER_ADMIN_ID, 1, 100000 ).length );
    }

    private void test_sproc_CheckForFileDocs( DatabaseService dbService ) {
        int[] documentIds = new int[]{DOC_FIRST_TEST_ID, DOC_TEST_SECOND_ID, DOC_TEST_THIRD_ID_FILE_DOC_TYPE, DOC_TEST_ID_DETACHED};
        int[] fileDocumentIds = dbService.sproc_CheckForFileDocs( documentIds );
        assertEquals( 1, fileDocumentIds.length );
        assertEquals( DOC_TEST_THIRD_ID_FILE_DOC_TYPE, fileDocumentIds[0] );
    }

    private void test_sproc_getChilds( DatabaseService dbService ) {
        DatabaseService.View_ChildData[] children = dbService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID );
        assertEquals( 0, children.length );

        children = dbService.sproc_getChilds( DOC_FIRST_TEST_ID, USER_ADMIN_ID );
        assertEquals( 1, children.length );
    }

    private void test_sproc_CheckAdminRights( DatabaseService dbService ) {
        assertTrue( dbService.sproc_CheckAdminRights( USER_ADMIN_ID ) );
        assertFalse( dbService.sproc_CheckAdminRights( USER_USER_ID ) );
    }

    private void test_sproc_CheckUserDocSharePermission( DatabaseService dbService ) {
        assertTrue( dbService.sproc_CheckUserDocSharePermission( USER_ADMIN_ID, DOC_FIRST_TEST_ID ) );
        assertFalse( dbService.sproc_CheckUserDocSharePermission( USER_USER_ID, DOC_FIRST_TEST_ID ) );
    }

    private void test_sproc_checkUserAdminrole( DatabaseService dbService ) {
        assertFalse( dbService.sproc_checkUserAdminrole( USER_USER_ID, 2 ) );
        assertTrue( dbService.sproc_checkUserAdminrole( USER_ADMIN_ID, 1 ) );
        assertFalse( dbService.sproc_checkUserAdminrole( USER_ID_NON_EXISTING, 2 ) );
    }

    private void test_sproc_GetFileName( DatabaseService databaseService ) {
        assertNull( databaseService.sproc_GetFileName(DOC_ID_FIRST_PAGE) ) ;
        assertNull( databaseService.sproc_GetFileName(DOC_ID_NON_EXISTING) ) ;
        assertTrue( DOC_THIRD_DOC_FILENAME.equals(databaseService.sproc_GetFileName(DOC_TEST_THIRD_ID_FILE_DOC_TYPE))) ;
    }

    private void test_sproc_GetDocType( DatabaseService databaseService) {
        assertEquals( DOC_TYPE_TEXT_ID, databaseService.sproc_GetDocType( DOC_FIRST_TEST_ID ));
        assertEquals( DOC_TYPE_FILE_ID , databaseService.sproc_GetDocType( DOC_TEST_THIRD_ID_FILE_DOC_TYPE ));
    }

    public void test_sproc_AddNewuser() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            DatabaseService.Table_users user = static_createDummyUser();
            DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
            dbService.sproc_AddNewuser( user );
            DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
            assertTrue( usersAfter.length == usersBefore.length + 1 );
        }
    }

    public void test_sproc_updateUser() {
        DatabaseService.Table_users user = static_createDummyUser();
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            dbService.sproc_AddNewuser( user );
            int rowCount = dbService.sproc_updateUser( user );
            assertEquals( 1, rowCount );
            DatabaseService.Table_users modifiedUser = getUser( dbService, USER_NEXT_FREE_ID );
            assertEquals( user, modifiedUser );
        }
    }

    public void test_sproc_delUser() {
        DatabaseService.Table_users user = static_createDummyUser();
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            DatabaseService.Table_users[] usersBefore = dbService.sproc_GetAllUsers_OrderByLastName();
            dbService.sproc_AddNewuser( user );
            int rowsAffected = dbService.sproc_delUser( USER_NEXT_FREE_ID );
            assertTrue( rowsAffected > 0 );
            DatabaseService.Table_users[] usersAfter = dbService.sproc_GetAllUsers_OrderByLastName();
            static_assertEquals( usersBefore, usersAfter );
        }
    }

    public void test_sproc_phoneNbrAdd() {
        DatabaseService.Table_users user = static_createDummyUser();
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            dbService.sproc_AddNewuser( user );
            int rowCount = dbService.sproc_phoneNbrAdd( USER_TEST_ID, "1234567", PHONE_TYPE_OTHER );
            assertEquals( 1, rowCount );
        }
    }

    public void test_sproc_PhoneNbrUpdate() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            int rowCount = dbService.sproc_PhoneNbrUpdate( USER_USER_ID, NEXT_FREE_PHONE_ID, "666666", PHONE_TYPE_HOME );
            assertEquals( 0, rowCount );
            dbService.sproc_phoneNbrAdd( USER_TEST_ID, "034985", PHONE_TYPE_OTHER );
            rowCount = dbService.sproc_PhoneNbrUpdate( USER_TEST_ID, NEXT_FREE_PHONE_ID, "666666", PHONE_TYPE_HOME );
            assertEquals( 1, rowCount );
        }
    }

    public void test_sproc_DelPhoneNr() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            int rowCount = dbService.sproc_DelPhoneNr( 2 );
            assertEquals( 0, rowCount );
            dbService.sproc_phoneNbrAdd( USER_USER_ID, "9887655", PHONE_TYPE_OTHER );
            dbService.sproc_phoneNbrAdd( USER_USER_ID, "123456", PHONE_TYPE_HOME );
            rowCount = dbService.sproc_DelPhoneNr( USER_USER_ID );
            assertEquals( 2, rowCount );
        }
    }

    public void test_sproc_PhoneNbrDelete() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            int rowCount = dbService.sproc_PhoneNbrDelete( NEXT_FREE_PHONE_ID );
            assertEquals( 0, rowCount );
            dbService.sproc_phoneNbrAdd( USER_TEST_ID, "9887655", 0 );
            dbService.sproc_phoneNbrAdd( USER_TEST_ID, "123456", 1 );
            rowCount = dbService.sproc_PhoneNbrDelete( NEXT_FREE_PHONE_ID );
            rowCount += dbService.sproc_PhoneNbrDelete( NEXT_FREE_PHONE_ID + 1 );
            assertEquals( 2, rowCount );
        }
    }

    public void test_sproc_AddUseradminPermissibleRoles() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            assertEquals( 1, databaseServices[i].sproc_AddUseradminPermissibleRoles( USER_TEST_ID, ROLE_TEST_ID ) );
        }
    }

    public void test_sproc_ChangeUserActiveStatus() {
        DatabaseService.Table_users user = static_createDummyUser();
        user.active = 1;
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            dbService.sproc_AddNewuser( user );

            dbService.sproc_ChangeUserActiveStatus( USER_TEST_ID, false );
            DatabaseService.Table_users modifiedUser = getUser( dbService, USER_TEST_ID );
            assertEquals( 0, modifiedUser.active );

            dbService.sproc_ChangeUserActiveStatus( USER_TEST_ID, true );
            DatabaseService.Table_users modifiedUser2 = getUser( dbService, USER_TEST_ID );
            assertEquals( 1, modifiedUser2.active );
        }
    }

    public void test_sproc_AddUserRole() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            assertEquals( 0, databaseService.sproc_AddUserRole( USER_ADMIN_ID, ROLE_SUPER_ADMIN_ID ) );
            assertEquals( 1, databaseService.sproc_AddUserRole( USER_TEST_ID, ROLE_USER_ADMIN_ID ) );
            assertEquals( 0, databaseService.sproc_AddUserRole( USER_TEST_ID, ROLE_USER_ADMIN_ID ) );
        }
    }

    public void test_sproc_DocumentDelete() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            assertEquals( 5, databaseServices[i].sproc_DocumentDelete( DOC_ID_FIRST_PAGE ) );
        }
    }

    public void test_sproc_AddExistingDocToMenu() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            int linksBefore = databaseService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID ).length;

            int doc_menu_no = 1;
            int rowCount = databaseService.sproc_AddExistingDocToMenu( DOC_TEST_ID_DETACHED, DOC_TEST_ID_DETACHED, doc_menu_no );
            assertEquals( 1, rowCount );

            int linksAfter = databaseService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID ).length;
            assertEquals( linksBefore + 1, linksAfter );
        }
    }

    public void test_deleteUserRole() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            assertEquals( 0, databaseService.sproc_DelUserRoles( USER_NEXT_FREE_ID, ROLE_NEXT_FREE_ID ) );
            assertEquals( 1, databaseService.sproc_AddUserRole( USER_TEST_ID, ROLE_TEST_ID ) );
            assertEquals( 1, databaseService.sproc_DelUserRoles( USER_TEST_ID, ROLE_TEST_ID ) );
        }
    }

    // Below is helper functions to more than one test.

    private static DatabaseService.Table_users static_createDummyUser() {
        DatabaseService.Table_users user = new DatabaseService.Table_users( USER_NEXT_FREE_ID, "test login name", "test password", "First name", "Last name", "Titel", "Company", "Adress", "City", "Zip", "Country", "Country council", "Email adress", 0, DOC_ID_FIRST_PAGE, 0, 1, 1, 1, new Timestamp( new java.util.Date().getTime() ) );
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
        dbService.initDatabase();
        dbService.createTestData();
        return dbService;
    }

    private static DatabaseService static_initSqlServer() throws IOException {
        DatabaseService dbService = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        dbService.initDatabase();
        dbService.createTestData();
        return dbService;
    }

    private static DatabaseService static_initMySql() throws IOException {
        DatabaseService dbService = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        dbService.initDatabase();
        dbService.createTestData();
        return dbService;
    }
}
