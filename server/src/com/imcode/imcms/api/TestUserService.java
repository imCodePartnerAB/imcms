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
    private UserDomainObject internalUser;

    private static int HIGHEST_USER_ID = 3 ;

    protected void setUp() throws Exception {
        super.setUp();

        contentManagementSystem = new MockContentManagementSystem();

        internalUser = new UserDomainObject();
        internalUser.setId( HIGHEST_USER_ID );
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

        internalUser.addRole( RoleDomainObject.SUPERADMIN );
        database.addExpectedSqlCall(new MockDatabase.EqualsSqlCallPredicate(ImcmsAuthenticatorAndUserAndRoleMapper.SPROC_GET_HIGHEST_USER_ID), ""+HIGHEST_USER_ID+1) ;

        User user = userService.createNewUser( "test", "test" );
        user.addRole( new Role( RoleDomainObject.SUPERADMIN ) );
        userService.saveUser( user );

        database.verifyExpectedSqlCalls() ;
        database.assertCalled( new MockDatabase.MatchesRegexSqlCallPredicate( "role" ) ) ;
    }

    public void testNonAdminCantCreateUser() throws SaveException, NoPermissionException {
        User user = userService.createNewUser( "test", "test" ) ;
        user.addRole( new Role( RoleDomainObject.SUPERADMIN ));
        database.addExpectedSqlCall( new MockDatabase.ProcedureSqlCallPredicate( ImcmsAuthenticatorAndUserAndRoleMapper.SPROC_GET_HIGHEST_USER_ID ), ""+HIGHEST_USER_ID+1 );
        try {
            userService.saveUser( user );
            fail() ;
        } catch( NoPermissionException ex ) {}
    }

    public void testNonAdminCantEditOtherUsers() throws NoPermissionException, SaveException {
        UserDomainObject otherInternalUser = new UserDomainObject();
        otherInternalUser.setId( HIGHEST_USER_ID + 1 );
        User otherUser = new User( otherInternalUser );
        try {
            userService.saveUser( otherUser );
            fail() ;
        } catch( NoPermissionException ex ) {}
    }

    public void testUserCanEditSelf() throws SaveException, NoPermissionException {
        internalUser.addRole( RoleDomainObject.SUPERADMIN );
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
    }

    public void testCreateNewRole() throws SaveException, NoPermissionException {
        internalUser.addRole( RoleDomainObject.SUPERADMIN );
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( ImcmsAuthenticatorAndUserAndRoleMapper.SQL_INSERT_INTO_ROLES ), "3" );
        String roleName = "test role";
        Role newRole = userService.createNewRole( roleName ) ;
        userService.saveRole( newRole );
        database.verifyExpectedSqlCalls();
        database.assertCalled( new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate( "roles", roleName ) );
    }

}
