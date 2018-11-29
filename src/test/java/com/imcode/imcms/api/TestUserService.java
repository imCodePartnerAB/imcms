package com.imcode.imcms.api;

import com.imcode.db.DatabaseException;
import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.servlet.LoginPasswordManager;
import imcode.server.LanguageMapper;
import imcode.server.MockImcmsServices;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;
import org.apache.commons.dbutils.ResultSetHandler;

public class TestUserService extends TestCase {

    private final static int HIGHEST_USER_ID = 3;
    private UserService userService;
    private MockContentManagementSystem contentManagementSystem;
    private MockDatabase database;
    private UserDomainObject internalUser;

    protected void setUp() throws Exception {
        super.setUp();

        contentManagementSystem = new MockContentManagementSystem();

        internalUser = new UserDomainObject(HIGHEST_USER_ID);
        contentManagementSystem.setCurrentUser(new User(internalUser));

        MockImcmsServices mockImcmsServices = new MockImcmsServices();
        database = new MockDatabase();
        mockImcmsServices.setDatabase(database);
        mockImcmsServices.setLanguageMapper(new LanguageMapper(database, "eng"));
        mockImcmsServices.setProcedureExecutor(new MockProcedureExecutor(database));
        LoginPasswordManager userLoginPasswordManager = new LoginPasswordManager();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(mockImcmsServices, userLoginPasswordManager);
        mockImcmsServices.setImcmsAuthenticatorAndUserAndRoleMapper(imcmsAuthenticatorAndUserAndRoleMapper);
        contentManagementSystem.setInternal(mockImcmsServices);
        mockImcmsServices.setRoleGetter(new MockRoleGetter());
        userService = new UserService(contentManagementSystem);
    }

    public void testUserCanEditSelf() throws SaveException, NoPermissionException {
        String loginName = "loginName";
        String firstName = "firstName";

        internalUser.setLoginName(loginName);
        internalUser.setFirstName(firstName);
        internalUser.setLastName("lastName");

        User user = contentManagementSystem.getCurrentUser();

        String newLoginName = "newLoginName";
        String newFirstName = "newFirstName";
        assertEquals(loginName, user.getLoginName());
        assertEquals(firstName, user.getFirstName());
        user.setLoginName(newLoginName);
        user.setFirstName(newFirstName);
        userService.saveUser(user);

        database.assertCalled("User can update contents of users table.", new MockDatabase.UpdateTableSqlCallPredicate("users", "" + HIGHEST_USER_ID));
        database.assertNotCalled("Old login name set.", new MockDatabase.UpdateTableSqlCallPredicate("users", loginName));
        database.assertCalled("New login name not set.", new MockDatabase.UpdateTableSqlCallPredicate("users", newLoginName));
        database.assertNotCalled("Old first name set.", new MockDatabase.UpdateTableSqlCallPredicate("users", firstName));
        database.assertCalled("New first name not set.", new MockDatabase.UpdateTableSqlCallPredicate("users", newFirstName));
        database.assertCalled("User can change own roles.", new MockDatabase.MatchesRegexSqlCallPredicate("role"));

        internalUser.addRoleId(Roles.SUPER_ADMIN.getId());
        userService.saveUser(user);
        database.assertCalled("Superadmin can change own roles.", new MockDatabase.MatchesRegexSqlCallPredicate("role"));
    }

    public void testCreateNewRole() throws SaveException, NoPermissionException {
        internalUser.addRoleId(Roles.SUPER_ADMIN.getId());
        database.addExpectedSqlCall(new MockDatabase.EqualsSqlCallPredicate(ImcmsAuthenticatorAndUserAndRoleMapper.SQL_INSERT_INTO_ROLES), 3);
        String roleName = "test role";
        Role newRole = userService.createNewRole(roleName);
        userService.saveRole(newRole);
        database.assertExpectedSqlCalls();
        database.assertCalled(new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate("roles", roleName));
    }

    public void testGetRoleByName() throws NoPermissionException {
        assertNull(userService.getRole(""));
    }

    public void testGetRoleById() throws NoPermissionException {
        assertNull(userService.getRole(1));
    }

    public static class MockProcedureExecutor implements ProcedureExecutor {

        private final MockDatabase database;

        MockProcedureExecutor(MockDatabase database) {
            this.database = database;
        }

        public int executeUpdateProcedure(String procedureName, Object[] parameters) throws DatabaseException {
            return database.executeUpdate(procedureName, parameters);
        }

        public <T> T executeProcedure(String procedureName, Object[] params, ResultSetHandler<T> resultSetHandler) {
            return database.executeQuery(procedureName, params, resultSetHandler);
        }
    }
}
