package com.imcode.imcms.api;

import imcode.server.ImcmsServices;
import imcode.server.MockImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import junit.framework.TestCase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;

import java.util.regex.Pattern;

public class TestUserService extends TestCase {

    private UserService userService ;
    private MockContentManagementSystem contentManagementSystem;
    private MockImcmsServices mockImcmsServices;

    protected void setUp() throws Exception {
        super.setUp();

        contentManagementSystem = new MockContentManagementSystem();

        UserDomainObject internalUser = new UserDomainObject();
        internalUser.addRole( RoleDomainObject.SUPERADMIN );
        contentManagementSystem.setCurrentUser( new User( internalUser ) );

        mockImcmsServices = new MockImcmsServices();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( mockImcmsServices );
        mockImcmsServices.setImcmsAuthenticatorAndUserAndRoleMapper( imcmsAuthenticatorAndUserAndRoleMapper );
        contentManagementSystem.setInternal(mockImcmsServices) ;

        userService = new UserService(contentManagementSystem);
    }

    public void testNewUserCanHaveRoles() throws SaveException, NoPermissionException {

        mockImcmsServices.addExpectedSqlCall(new MockImcmsServices.SqlCall(ImcmsAuthenticatorAndUserAndRoleMapper.SPROC_GET_HIGHEST_USER_ID, null, "3")) ;

        User user = userService.createNewUser( "test", "test" );
        user.addRole( new Role( RoleDomainObject.SUPERADMIN ) );
        userService.saveUser( user );

        mockImcmsServices.verifyExpectedSqlCalls() ;
        assertTrue( containsSqlCall( new SqlCallStringPredicate( "role" ) ) ) ;
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

        assertTrue( "User can update contents of users table.", containsSqlCall( new UpdateTableSqlCallPredicate( "users", "3" ) )) ;
        assertFalse( "Old login name set.", containsSqlCall( new UpdateTableSqlCallPredicate( "users", loginName ) ) );
        assertTrue( "New login name not set.", containsSqlCall( new UpdateTableSqlCallPredicate( "users", newLoginName ) ) );
        assertFalse( "Old first name set.", containsSqlCall( new UpdateTableSqlCallPredicate( "users", firstName ) ) );
        assertTrue( "New first name not set.", containsSqlCall( new UpdateTableSqlCallPredicate( "users", newFirstName ) ) );
        assertFalse( "User can not change own roles.", containsSqlCall( new SqlCallStringPredicate( "role" ) ) );
    }

    public void testCreateNewRole() throws SaveException, NoPermissionException {
        mockImcmsServices.addExpectedSqlCall( new MockImcmsServices.SqlCall( ImcmsAuthenticatorAndUserAndRoleMapper.SQL_INSERT_INTO_ROLES, null, "3" ) );
        String roleName = "test role";
        Role newRole = userService.createNewRole( roleName ) ;
        userService.saveRole( newRole );
        mockImcmsServices.verifyExpectedSqlCalls();
        assertTrue( containsSqlCall( new InsertTableSqlCallPredicate( "roles", roleName ) ) );
    }

    private boolean containsSqlCall( Predicate predicate ) {
        return CollectionUtils.exists( mockImcmsServices.getSqlCalls(), predicate);
    }

    private static class MockContentManagementSystem extends ContentManagementSystem {

        private ImcmsServices imcmsServices;
        private User currentUser;

        SecurityChecker getSecurityChecker() {
            return new SecurityChecker( this );
        }

        public UserService getUserService() {
            return null;  // TODO
        }

        public DocumentService getDocumentService() {
            return null;  // TODO
        }

        public User getCurrentUser() {
            return currentUser ;
        }

        public DatabaseService getDatabaseService() {
            return null;  // TODO
        }

        public TemplateService getTemplateService() {
            return null;  // TODO
        }

        public MailService getMailService() {
            return null;  // TODO
        }

        ImcmsServices getInternal() {
            return imcmsServices ;
        }

        public void setInternal(ImcmsServices imcmsServices) {
            this.imcmsServices = imcmsServices ;
        }

        public void setCurrentUser( User user ) {
            currentUser = user ;
        }
    }

    private abstract static class SqlCallPredicate implements Predicate {

        public boolean evaluate( Object object ) {
            return evaluateSqlCall((MockImcmsServices.SqlCall)object) ;
        }

        abstract boolean evaluateSqlCall( MockImcmsServices.SqlCall sqlCall ) ;

    }

    private static class UpdateTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;
        private String parameter;

        UpdateTableSqlCallPredicate( String tableName, String parameter ) {
            this.tableName = tableName;
            this.parameter = parameter;
        }

        boolean evaluateSqlCall( MockImcmsServices.SqlCall sqlCall ) {
            boolean stringMatchesUpdateTableName = Pattern.compile( "^update\\s+" + tableName ).matcher( sqlCall.getString().toLowerCase() ).find();
            boolean parametersContainsParameter = ArrayUtils.contains( sqlCall.getParameters(), parameter );
            return stringMatchesUpdateTableName && parametersContainsParameter;
        }
    }

    private static class InsertTableSqlCallPredicate extends SqlCallPredicate {

        private String tableName;
        private String parameter;

        InsertTableSqlCallPredicate( String tableName, String parameter ) {
            this.tableName = tableName;
            this.parameter = parameter;
        }

        boolean evaluateSqlCall( MockImcmsServices.SqlCall sqlCall ) {
            boolean stringMatchesUpdateTableName = Pattern.compile( "^insert\\s+(?:into\\s+)?" + tableName ).matcher( sqlCall.getString().toLowerCase() ).find();
            boolean parametersContainsParameter = ArrayUtils.contains( sqlCall.getParameters(), parameter );
            return stringMatchesUpdateTableName && parametersContainsParameter;
        }
    }

    private static class SqlCallStringPredicate extends SqlCallPredicate {

        private String string;

        SqlCallStringPredicate( String string ) {
            this.string = string;
        }

        boolean evaluateSqlCall( MockImcmsServices.SqlCall sqlCall ) {
            return Pattern.compile( string ).matcher( sqlCall.getString().toLowerCase() ).find();
        }
    }
}
