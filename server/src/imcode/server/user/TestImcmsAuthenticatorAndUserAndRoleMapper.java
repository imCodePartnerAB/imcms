package imcode.server.user;

import imcode.server.db.MockDatabase;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddExternalUser() {
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

        private String[] updateQueryParameters;

        public String sqlProcedureStr( String procedure, String[] params ) {
            return "3" ;
        }

        public int sqlUpdateQuery( String sqlStr, String[] params ) {
            this.updateQueryParameters = params ;
            return 1;
        }
    }
}