package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.db.MockDatabase;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestUserService extends TestCase {

    private UserService userService ;
    private MockContentManagementSystem contentManagementSystem;
    private MockImcmsServices mockImcmsServices;
    private MockDatabase database;

    protected void setUp() throws Exception {
        super.setUp();

        contentManagementSystem = new MockContentManagementSystem();

        UserDomainObject internalUser = new UserDomainObject();
        internalUser.addRole( RoleDomainObject.SUPERADMIN );
        contentManagementSystem.setCurrentUser( new User( internalUser ) );

        mockImcmsServices = new MockImcmsServices();
        database = new MockDatabase();
        mockImcmsServices.setDatabase( database );
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( mockImcmsServices, mockImcmsServices );
        mockImcmsServices.setImcmsAuthenticatorAndUserAndRoleMapper( imcmsAuthenticatorAndUserAndRoleMapper );
        contentManagementSystem.setInternal(mockImcmsServices) ;

        userService = new UserService(contentManagementSystem);
    }

    public void testNewUserCanHaveRoles() throws SaveException, NoPermissionException {

        database.addExpectedSqlCall(new MockDatabase.EqualsSqlCallPredicate(ImcmsAuthenticatorAndUserAndRoleMapper.SPROC_GET_HIGHEST_USER_ID), "3") ;

        User user = userService.createNewUser( "test", "test" );
        user.addRole( new Role( RoleDomainObject.SUPERADMIN ) );
        userService.saveUser( user );

        database.verifyExpectedSqlCalls() ;
        database.assertCalled( new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) ) ;
    }

    public void testUserCanEditSelf() throws SaveException, NoPermissionException {
        String loginName = "loginName";
        String firstName = "firstName";

        UserDomainObject internalUser = new UserDomainObject();
        internalUser.setId( 3 );
        internalUser.setLoginName( loginName );
        internalUser.setFirstName( firstName );
        internalUser.setLastName( "lastName" );
        contentManagementSystem.setCurrentUser( new User( internalUser ) );

        User user = contentManagementSystem.getCurrentUser() ;

        String newLoginName = "newLoginName";
        String newFirstName = "newFirstName";
        assertEquals( loginName, user.getLoginName() );
        assertEquals( firstName, user.getFirstName() );
        user.setLoginName( newLoginName );
        user.setFirstName( newFirstName );
        userService.saveUser( user );

        database.assertCalled( "User can update contents of users table.", new MockDatabase.UpdateTableSqlCallPredicate( "users", "3" ) ) ;
        database.assertNotCalled( "Old login name set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", loginName ) );
        database.assertCalled( "New login name not set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", newLoginName ) );
        database.assertNotCalled( "Old first name set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", firstName ) );
        database.assertCalled( "New first name not set.", new MockDatabase.UpdateTableSqlCallPredicate( "users", newFirstName ) );
        database.assertNotCalled( "User can not change own roles.", new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) );
    }

    public void testCreateNewRole() throws SaveException, NoPermissionException {
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( ImcmsAuthenticatorAndUserAndRoleMapper.SQL_INSERT_INTO_ROLES ), "3" );
        String roleName = "test role";
        Role newRole = userService.createNewRole( roleName ) ;
        userService.saveRole( newRole );
        database.verifyExpectedSqlCalls();
        database.assertCalled( new MockDatabase.InsertTableSqlCallPredicate( "roles", roleName ) );
    }

}
