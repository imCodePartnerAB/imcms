package imcode.server.user;

import imcode.server.MockImcmsServices;
import junit.framework.TestCase;

import com.imcode.db.mock.MockDatabase;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddExternalUser() throws UserAlreadyExistsException {
        MockDatabase database = new MockDatabase();
        database.addExpectedSqlCall(new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate("users", "Test"), new Integer(1));
        MockImcmsServices mockImcmsServices = new MockImcmsServices();
        mockImcmsServices.setDatabase(database);
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper(mockImcmsServices);
        UserDomainObject user = new UserDomainObject();
        user.setLoginName( "Test" );
        user.setImcmsExternal( true );
        user.setLanguageIso639_2( "eng" );
        imcmsAuthenticatorAndUserAndRoleMapper.addUser( user);
        database.assertExpectedSqlCalls();
    }

}