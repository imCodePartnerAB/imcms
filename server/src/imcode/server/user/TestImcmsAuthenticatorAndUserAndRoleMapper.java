package imcode.server.user;

import imcode.server.db.impl.MockDatabase;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddExternalUser() throws UserAlreadyExistsException {
        AddUserMockDatabase database = new AddUserMockDatabase();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( database, null );
        UserDomainObject user = new UserDomainObject();
        user.setLoginName( "Test" );
        user.setImcmsExternal( true );
        user.setLanguageIso639_2( "eng" );
        imcmsAuthenticatorAndUserAndRoleMapper.addUser( user, null );
        assertNotNull( database.updateQueryParameters );
        assertFalse(ArrayUtils.contains( database.updateQueryParameters, null )) ;
    }

    private static class AddUserMockDatabase extends MockDatabase {

        private Object[] updateQueryParameters;

        public String executeStringProcedure( String procedure, String[] params ) {
            return "3" ;
        }

        public int executeUpdateQuery( String sqlStr, Object[] params ) {
            this.updateQueryParameters = params ;
            return 1;
        }
    }
}