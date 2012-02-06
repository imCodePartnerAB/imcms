package imcode.server.user;

import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.servlet.UserLoginPasswordManager;
import imcode.server.MockImcmsServices;
import junit.framework.TestCase;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddExternalUser() throws UserAlreadyExistsException {
        MockDatabase database = new MockDatabase();
        database.addExpectedSqlCall(new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate("users", "Test"), new Integer(1));
        MockImcmsServices mockImcmsServices = new MockImcmsServices();
        mockImcmsServices.setDatabase(database);
        UserLoginPasswordManager userLoginPasswordManager = new UserLoginPasswordManager();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(mockImcmsServices, userLoginPasswordManager);
        UserDomainObject user = new UserDomainObject();
        user.setLoginName( "Test" );
        user.setImcmsExternal( true );
        user.setLanguageIso639_2( "eng" );
        imcmsAuthenticatorAndUserAndRoleMapper.addUser( user);
        database.assertExpectedSqlCalls();
    }

}