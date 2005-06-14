package imcode.server.user;

import imcode.server.db.impl.MockDatabase;
import junit.framework.TestCase;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddExternalUser() throws UserAlreadyExistsException {
        MockDatabase database = new MockDatabase();
        database.addExpectedSqlCall(new MockDatabase.InsertIntoTableWithParameterSqlCallPredicate("users", "Test"), new Integer(1));

        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( database, null );
        UserDomainObject user = new UserDomainObject();
        user.setLoginName( "Test" );
        user.setImcmsExternal( true );
        user.setLanguageIso639_2( "eng" );
        imcmsAuthenticatorAndUserAndRoleMapper.addUser( user, null );
        database.verifyExpectedSqlCalls();
    }

}