package imcode.server.user;

import imcode.server.db.IntegrityConstraintViolationSQLException;
import imcode.server.db.MockDatabase;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

public class TestImcmsAuthenticatorAndUserAndRoleMapper extends TestCase {

    public void testAddRoleNameTwice() throws Exception {
        MockDatabase database = new AddRoleMockDatabase();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( database, null );
        imcmsAuthenticatorAndUserAndRoleMapper.addRole( "Test" ) ;
        imcmsAuthenticatorAndUserAndRoleMapper.addRole( "Test" ) ;
    }

    public void testAddRoleTwice() {
        MockDatabase database = new AddRoleMockDatabase();
        ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = new ImcmsAuthenticatorAndUserAndRoleMapper( database, null );
        RoleDomainObject role = new RoleDomainObject( 3, "Test", 0 );
        imcmsAuthenticatorAndUserAndRoleMapper.addRole( role );
        try {
            imcmsAuthenticatorAndUserAndRoleMapper.addRole( role );
            fail("Should have thrown exception on second attempt to add same role.") ;
        } catch( IntegrityConstraintViolationSQLException icvse ) {}
    }

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

    private static class AddRoleMockDatabase extends MockDatabase {

        boolean roleInserted ;
        public String sqlQueryStr( String sqlStr, String[] params ) {
            if (roleInserted) {
                throw new IntegrityConstraintViolationSQLException( null ) ;
            }
            roleInserted = true ;
            return "3";
        }

        public String[] sqlQuery( String sqlStr, String[] params ) {
            if (roleInserted && ImcmsAuthenticatorAndUserAndRoleMapper.SQL_SELECT_ROLE_BY_NAME.equals( sqlStr )) {
                return new String[] { "3", "Test", "0", "0" } ;
            }
            return new String[0] ;
        }
    }
}