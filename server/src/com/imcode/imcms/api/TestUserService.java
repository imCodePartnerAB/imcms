package com.imcode.imcms.api;

import imcode.server.LanguageMapper;
import imcode.server.MockImcmsServices;
import imcode.server.db.impl.MockDatabase;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestUserService extends TestCase {

    private UserService userService ;
    private MockContentManagementSystem contentManagementSystem;
    private MockDatabase database;
    private UserDomainObject internalUser;

    private static int HIGHEST_USER_ID = 3 ;
    private MockImcmsServices mockImcmsServices;

    protected void setUp() throws Exception {
        super.setUp();

        contentManagementSystem = new MockContentManagementSystem();

        internalUser = new UserDomainObject(HIGHEST_USER_ID);
        contentManagementSystem.setCurrentUser( new User( internalUser ) );

        mockImcmsServices = new MockImcmsServices();
        database = new MockDatabase();
        mockImcmsServices.setDatabase( database );
        mockImcmsServices.setLanguageMapper(new LanguageMapper(database, "eng")) ;
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( database, null);
        mockImcmsServices.setImcmsAuthenticatorAndUserAndRoleMapper( imcmsAuthenticatorAndUserAndRoleMapper );
        contentManagementSystem.setInternal(mockImcmsServices) ;
        mockImcmsServices.setRoleGetter(new MockRoleGetter());
        userService = new UserService(contentManagementSystem);
    }

    public void testGetUser() throws NoPermissionException {
        assertNull(userService.getUser("noone")) ;
    }

    public void testNewUserCanHaveRoles() throws SaveException, NoPermissionException {

        internalUser.addRoleId( RoleId.SUPERADMIN );
        database.addExpectedSqlCall(new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate("users", "test"), new Integer(HIGHEST_USER_ID+1)) ;

        User user = userService.createNewUser( "test", "test" );
        user.addRole( new Role( mockImcmsServices.getRoleGetter().getRole(RoleId.SUPERADMIN) ) );
        userService.saveUser( user );

        database.assertExpectedSqlCalls() ;
        database.assertCalled( new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) ) ;
    }

    public void testNonAdminCantCreateUser() throws SaveException {
        User user = userService.createNewUser( "test", "test" ) ;
        user.addRole( new Role( mockImcmsServices.getRoleGetter().getRole(RoleId.SUPERADMIN) ));
        try {
            userService.saveUser( user );
            fail() ;
        } catch( NoPermissionException ex ) {}
        database.assertExpectedSqlCalls();
    }

    public void testNonAdminCantEditOtherUsers() throws SaveException {
        UserDomainObject otherInternalUser = new UserDomainObject(HIGHEST_USER_ID + 1);
        User otherUser = new User( otherInternalUser );
        try {
            userService.saveUser( otherUser );
            fail() ;
        } catch( NoPermissionException ex ) {}
    }

    public void testUserCanEditSelf() throws SaveException, NoPermissionException {
        String loginName = "loginName";
        String firstName = "firstName";

        internalUser.setLoginName( loginName );
        internalUser.setFirstName( firstName );
        internalUser.setLastName( "lastName" );

        User user = contentManagementSystem.getCurrentUser() ;

        String newLoginName = "newLoginName";
        String newFirstName = "newFirstName";
        assertEquals( loginName, user.getLoginName() );
        assertEquals( firstName, user.getFirstName() );
        user.setLoginName( newLoginName );
        user.setFirstName( newFirstName );
        userService.saveUser( user );

        database.assertCalled( "User can update contents of users table.", new MockDatabase.UpdateTableSqlCallPredicate( "users", ""+HIGHEST_USER_ID ) ) ;
        database.assertNotCalled( "Old login name set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", loginName ) );
        database.assertCalled( "New login name not set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", newLoginName ) );
        database.assertNotCalled( "Old first name set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", firstName ) );
        database.assertCalled( "New first name not set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", newFirstName ) );
        database.assertNotCalled( "User can not change own roles.", new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) );

        internalUser.addRoleId( RoleId.SUPERADMIN );
        userService.saveUser( user );
        database.assertCalled( "Superadmin can change own roles.", new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) );
    }

    public void testCreateNewRole() throws SaveException, NoPermissionException {
        internalUser.addRoleId( RoleId.SUPERADMIN );
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( ImcmsAuthenticatorAndUserAndRoleMapper.SQL_INSERT_INTO_ROLES ), new Integer(3) );
        String roleName = "test role";
        Role newRole = userService.createNewRole( roleName ) ;
        userService.saveRole( newRole );
        database.assertExpectedSqlCalls();
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( "roles", roleName ) );
    }

    public void testGetAllRoles() {
        try {
            userService.getAllRoles() ;
            fail() ;
        } catch (NoPermissionException npe) {}
    }

    public void testGetRoleByName() throws NoPermissionException {
        assertNull( userService.getRole( "" ) ) ;
    }

    public void testGetRoleById() throws NoPermissionException {
        assertNull( userService.getRole( 1 ) ) ;
    }

}
