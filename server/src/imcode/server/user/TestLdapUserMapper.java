package imcode.server.user;

import java.util.Arrays;
import java.io.IOException;

public class TestLdapUserMapper extends LdapUserBaseTestCase {

    private LdapUserAndRoleMapper ldapUserAndRoleMapper;

    public void setUp() throws IOException {
        this.ldapUserAndRoleMapper = getLdapUserAndRoleMapper( new String[]{} );
    }

    public void testExistingUserLdapService() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( ldapUsername );
        assertNotNull( user );
        assertEquals( ldapUsername, user.getLoginName() );
        assertNull( user.getPassword() );
    }

    public void testAuthenticate() {
        boolean userAuthenticates = ldapUserAndRoleMapper.authenticate( ldapUsername, ldapPassword );
        assertTrue( userAuthenticates );
    }

    public void testInvalidName() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( "" );
        assertNull( user );
    }

    public void testNonExistingUser() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( "non-existing user" );
        assertNull( user );
    }

    public void testGetRolesForUserLdapService() {
        UserDomainObject user = ldapUserAndRoleMapper.getUser( ldapUsername );
        String[] roleNames = ldapUserAndRoleMapper.getRoleNames( user );
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
    }
   
    public void testGetAllRoleNames() {
        String[] roleNames = ldapUserAndRoleMapper.getAllRoleNames();
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
    }

}