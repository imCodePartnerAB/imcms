package imcode.server.db;

import imcode.server.test.Log4JConfiguredTestCase;

import java.sql.Timestamp;
import java.io.IOException;

public class TestDatabaseService extends Log4JConfiguredTestCase {

    private DatabaseService[] databaseServices = null;

    // If you change anything in the scripts that creates the database
    // you propably have to change some of the following constants.
    private final static int USER_ADMIN_ID = 1;
    private final static int USER_USER_ID = 2;
    private static final int USER_TEST_ID = 3;
    private static final int USER_NEXT_FREE_ID = 4;
    private static final int USER_ID_NON_EXISTING = 2452345;

    private static int ROLE_SUPER_ADMIN_ID = 0;
    private static int ROLE_USER_ADMIN_ID = 1;
    private static int ROLE_USERS_ID = 2;
    private static final int ROLE_TEST_ID = 3;
    private final static int ROLE_NEXT_FREE_ID = 4;
    private static final String ROLE_SUPER_ADMIN_NAME = "Superadmin";
    private static final String ROLE_USER_ADMIN_NAME = "Useradmin";
    private static final String ROLE_USERS_NAME = "Users";
    private static final String ROLE_TEST_NAME = "TestRole";

    private int DOC_NO_OF_DOCS = 8; // 1001 + folowing
    private static final int DOC_ID_FIRST_PAGE = 1001;
    private static final int DOC_ID_NON_EXISTING = 66666;
    private final static int DOC_TEST_FIRST_ID = 9001;
    private final static int DOC_TEST_SECOND_ID = 9002;
    private final static int DOC_TEST_THIRD_ID_FILE_DOC_TYPE = 9003;
    private final static int DOC_TEST_ID_DETACHED = 9999;
    private final static int DOC_TEST_MAX_ID = DOC_TEST_ID_DETACHED;
    private static final int DOC_NO_MORE_THAN_EXISTS_IN_SYSTEM = 100000;

    private static final int DOC_TYPE_TEXT_ID = 2;
    private static final int DOC_TYPE_FILE_ID = 8;
    private static final int DOC_TYPES_NUMBER_OF = 9;

    private static final String DOC_THIRD_DOC_FILENAME = "testfilename.txt";

    private int NEXT_FREE_PHONE_ID = 2;
    private static final int PHONE_TYPE_HOME = 1;
    private static final int PHONE_TYPE_OTHER = 0;

    private static final int LANG_ID_SWEDEN = 1;
    private static final String LANG_PREFIX_SWEDEN = "se";
    private static final int LANG_ID_ENGLAND = 2;
    private static final String LANG_PREFIX_ENGLAND = "en";
    private String USER_TEST_LOGIN_NAME = "TestUser";

    protected void setUp() throws IOException {
        databaseServices = new DatabaseService[]{
            DatabaseTestInitializer.static_initMySql(),
            DatabaseTestInitializer.static_initSqlServer(),
            DatabaseTestInitializer.static_initMimer(),
        };
    }

    /**
     * I do all of them together for performance reasons.
     */
    public void testNonModfyingTests() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            test_sproc_getAllRoles( databaseService );
            test_sproc_getAllUsers( databaseService );
            test_sproc_getTemplatesInGroup( databaseService );
            test_sproc_getHighestUserId( databaseService );
            test_sproc_GetPhonetypeName( databaseService );
            test_sproc_GetPhonetypes_ORDER_BY_phonetype_id( databaseService );
            test_sproc_GetUserPhoneNumbers( databaseService );
            test_sproc_GetUserPhones( databaseService );
            test_sproc_FindUserName( databaseService );
            test_sproc_FindMetaId( databaseService );
            test_sproc_getChilds( databaseService );
            test_sproc_getDocs( databaseService );
            test_sproc_CheckAdminRights( databaseService );
            test_sproc_CheckUserDocSharePermission( databaseService );
            test_sproc_checkUserAdminrole( databaseService );
            test_sproc_GetFileName( databaseService );
            test_sproc_GetDocType( databaseService );
            test_sproc_GetDocTypes( databaseService );
            test_sproc_GetDocTypesForUser( databaseService );
            test_sproc_GetText( databaseService );
            test_sproc_GetIncludes( databaseService );
            test_sproc_GetImgs( databaseService );
            test_sproc_GetUserRoles( databaseService );
            test_sproc_GetLangPrefixFromId( databaseService );
            test_sproc_GetUserInfo( databaseService );
            test_sproc_sproc_GetRoleIdByRoleName( databaseService );
            test_sproc_getTemplates( databaseService );
            test_sproc_GetTemplateId( databaseService );
            test_sproc_GetUserPassword( databaseService );
            test_sproc_getUserRoleIds( databaseService );
            test_sproc_GetUsersWhoBelongsToRole( databaseService );
            testIsFileDoc( databaseService );
        }
    }

    private void test_sproc_GetUsersWhoBelongsToRole( DatabaseService databaseService ) {
        assertEquals( 2, databaseService.sproc_GetUsersWhoBelongsToRole( ROLE_USERS_ID ).length );
    }

    private void test_sproc_getUserRoleIds( DatabaseService databaseService ) {
        assertEquals( 2, databaseService.sproc_getUserRoleIds( USER_TEST_ID ).length );
    }

    private void test_sproc_GetUserPassword( DatabaseService databaseService ) {
        assertEquals( "admin", databaseService.sproc_GetUserPassword( USER_ADMIN_ID ) );
        assertEquals( "", databaseService.sproc_GetUserPassword( USER_ID_NON_EXISTING ) );
    }

    private void test_sproc_GetTemplateId( DatabaseService databaseService ) {
        assertEquals( 1, databaseService.sproc_GetTemplateId( "Start" ) );
    }

    private void test_sproc_getTemplates(DatabaseService databaseService) {
        assertEquals( 5, databaseService.sproc_getTemplates().length );
    }

    private void test_sproc_sproc_GetRoleIdByRoleName( DatabaseService databaseService ) {
        assertEquals( ROLE_SUPER_ADMIN_ID, databaseService.sproc_GetRoleIdByRoleName( ROLE_SUPER_ADMIN_NAME ) );
        assertEquals( ROLE_USER_ADMIN_ID, databaseService.sproc_GetRoleIdByRoleName( ROLE_USER_ADMIN_NAME ) );
        assertEquals( ROLE_USERS_ID, databaseService.sproc_GetRoleIdByRoleName( ROLE_USERS_NAME ) );
        assertEquals( ROLE_TEST_ID, databaseService.sproc_GetRoleIdByRoleName( ROLE_TEST_NAME ) );
    }

    private void test_sproc_GetUserInfo( DatabaseService databaseService ) {
        assertNull( databaseService.sproc_GetUserInfo( USER_NEXT_FREE_ID ) );
        assertEquals( USER_TEST_ID, databaseService.sproc_GetUserInfo( USER_TEST_ID ).user_id );
        assertEquals( USER_TEST_LOGIN_NAME, databaseService.sproc_GetUserInfo( USER_TEST_ID ).login_name );
    }

    private void test_sproc_GetLangPrefixFromId( DatabaseService databaseService ) {
        assertEquals( LANG_PREFIX_SWEDEN, databaseService.sproc_GetLangPrefixFromId( LANG_ID_SWEDEN ).lang_prefix );
        assertEquals( LANG_PREFIX_ENGLAND, databaseService.sproc_GetLangPrefixFromId( LANG_ID_ENGLAND ).lang_prefix );
    }

    private void test_sproc_GetUserRoles( DatabaseService databaseService ) {
        assertEquals( 2, databaseService.sproc_GetUserRoles( ROLE_TEST_ID ).length );
    }

    private void test_sproc_GetImgs( DatabaseService databaseService ) {
        assertEquals( 1, databaseService.sproc_getImages( DOC_TEST_FIRST_ID ).length );
    }

    private void test_sproc_GetIncludes( DatabaseService databaseService ) {
        assertEquals( 1, databaseService.sproc_GetInclues( DOC_TEST_FIRST_ID ).length );
    }

    private void test_sproc_GetText( DatabaseService databaseService ) {
        assertNotNull( databaseService.sproc_GetText( DOC_ID_FIRST_PAGE, 1 ) );
        assertNull( databaseService.sproc_GetText( DOC_ID_FIRST_PAGE, 2 ) );
    }

    private void test_sproc_GetDocTypesForUser( DatabaseService dbService ) {
        DatabaseService.Table_doc_types[] userDocTypes = dbService.sproc_GetDocTypesForUser( USER_TEST_ID, DOC_TEST_FIRST_ID, LANG_PREFIX_SWEDEN );
        assertEquals( 1, userDocTypes.length );
    }

    private void testIsFileDoc( DatabaseService dbService ) {
        assertFalse( dbService.isFileDoc( DOC_TEST_FIRST_ID ) );
        assertTrue( dbService.isFileDoc( DOC_TEST_THIRD_ID_FILE_DOC_TYPE ) );
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
        assertEquals( 1, dbService.sproc_GetTemplatesInGroup( 0 ).length );
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
        assertEquals( DOC_NO_OF_DOCS, dbService.sproc_getDocs( USER_ADMIN_ID, 1, DOC_NO_MORE_THAN_EXISTS_IN_SYSTEM ).length );
    }

    private void test_sproc_getChilds( DatabaseService dbService ) {
        DatabaseService.MoreThanOneTable_ChildsAndMeta[] children = dbService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID );
        assertEquals( 0, children.length );

        children = dbService.sproc_getChilds( DOC_TEST_FIRST_ID, USER_ADMIN_ID );
        assertEquals( 1, children.length );
    }

    private void test_sproc_CheckAdminRights( DatabaseService dbService ) {
        assertTrue( dbService.sproc_CheckAdminRights( USER_ADMIN_ID ) );
        assertFalse( dbService.sproc_CheckAdminRights( USER_USER_ID ) );
    }

    private void test_sproc_CheckUserDocSharePermission( DatabaseService dbService ) {
        assertTrue( dbService.sproc_CheckUserDocSharePermission( USER_ADMIN_ID, DOC_TEST_FIRST_ID ) );
        assertFalse( dbService.sproc_CheckUserDocSharePermission( USER_USER_ID, DOC_TEST_FIRST_ID ) );
    }

    private void test_sproc_checkUserAdminrole( DatabaseService dbService ) {
        assertFalse( dbService.sproc_checkUserAdminrole( USER_USER_ID, 2 ) );
        assertTrue( dbService.sproc_checkUserAdminrole( USER_ADMIN_ID, 1 ) );
        assertFalse( dbService.sproc_checkUserAdminrole( USER_ID_NON_EXISTING, 2 ) );
    }

    private void test_sproc_GetFileName( DatabaseService databaseService ) {
        assertNull( databaseService.sproc_GetFileName( DOC_ID_FIRST_PAGE ) );
        assertNull( databaseService.sproc_GetFileName( DOC_ID_NON_EXISTING ) );
        assertTrue( DOC_THIRD_DOC_FILENAME.equals( databaseService.sproc_GetFileName( DOC_TEST_THIRD_ID_FILE_DOC_TYPE ) ) );
    }

    private void test_sproc_GetDocType( DatabaseService databaseService ) {
        assertEquals( DOC_TYPE_TEXT_ID, databaseService.sproc_GetDocType( DOC_TEST_FIRST_ID ) );
        assertEquals( DOC_TYPE_FILE_ID, databaseService.sproc_GetDocType( DOC_TEST_THIRD_ID_FILE_DOC_TYPE ) );
    }

    private void test_sproc_GetDocTypes( DatabaseService databaseService ) {
        assertEquals( DOC_TYPES_NUMBER_OF, databaseService.sproc_GetDocTypes( LANG_PREFIX_SWEDEN ).length );
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
        }
    }

    public void test_sproc_delUser() {
        DatabaseService.Table_users user = static_createDummyUser();
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService dbService = databaseServices[i];
            dbService.sproc_AddNewuser( user );
            int rowsAffected = dbService.sproc_delUser( USER_NEXT_FREE_ID );
            assertTrue( rowsAffected > 0 );
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
            DatabaseService.Table_users modifiedUser = dbService.getFromTable_users( new Integer( USER_TEST_ID ) );
            assertEquals( 0, modifiedUser.active );

            dbService.sproc_ChangeUserActiveStatus( USER_TEST_ID, true );
            DatabaseService.Table_users modifiedUser2 = dbService.getFromTable_users( new Integer( USER_TEST_ID ) );
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
            assertEquals( 2, rowCount );

            int linksAfter = databaseService.sproc_getChilds( DOC_TEST_ID_DETACHED, USER_ADMIN_ID ).length;
            assertEquals( linksBefore + 1, linksAfter );
        }
    }

    public void test_deleteUserRole() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            assertEquals( 0, databaseService.sproc_DelUserRoles( USER_NEXT_FREE_ID, ROLE_NEXT_FREE_ID ) );
            assertEquals( 1, databaseService.sproc_DelUserRoles( USER_TEST_ID, ROLE_TEST_ID ) );
            assertEquals( 1, databaseService.sproc_DelUserRoles( USER_TEST_ID, -1 ) );
            assertEquals( 0, databaseService.sproc_DelUserRoles( USER_TEST_ID, -1 ) );
        }
    }

    // todo: l�gg till testdata f�r samtliga doc typer
    public void test_sproc_copyDocs() {
        int[] documentsToBeCopied = new int[]{DOC_TEST_SECOND_ID, DOC_TEST_THIRD_ID_FILE_DOC_TYPE};
        int menu_id = 1;
        String copyPrefix = "Kopierat document";
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            DatabaseService.PartOfTable_document[] documentForUserBefore = databaseService.sproc_getDocs( USER_ADMIN_ID, DOC_TEST_FIRST_ID, DOC_NO_MORE_THAN_EXISTS_IN_SYSTEM );
            int[] result = databaseService.sproc_copyDocs( DOC_TEST_FIRST_ID, menu_id, USER_ADMIN_ID, documentsToBeCopied, copyPrefix );
            assertEquals( 1, result.length );
            DatabaseService.PartOfTable_document[] documentForUserAfter = databaseService.sproc_getDocs( USER_ADMIN_ID, DOC_TEST_FIRST_ID, DOC_NO_MORE_THAN_EXISTS_IN_SYSTEM );
            assertTrue( documentForUserBefore.length != documentForUserAfter.length );
        }
    }

    // todo: Fler testfall n�r det finns mer testdata.
    public void test_sproc_deleteInclude() {
        for( int i = 0; i < databaseServices.length; i++ ) {
            DatabaseService databaseService = databaseServices[i];
            assertEquals( 0, databaseService.sproc_deleteInclude( DOC_ID_NON_EXISTING, 1 ) );
        }
    }

    // Below is helper functions to more than one test.

    private static DatabaseService.Table_users static_createDummyUser() {
        DatabaseService.Table_users user = new DatabaseService.Table_users( USER_NEXT_FREE_ID, "test login name", "test password", "First name", "Last name", "Titel", "Company", "Adress", "City", "Zip", "Country", "Country council", "Email adress", 0, DOC_ID_FIRST_PAGE, 0, 1, 1, 1, new Timestamp( new java.util.Date().getTime() ) );
        return user;
    }
}
