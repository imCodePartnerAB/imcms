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
   
    /*
       public void testExistingUserChristoffer() {
          User user = ldapUserAndRoleMapper.getUser( "chrham" );
          assertNotNull( user );
          assertEquals( "chrham", user.getLoginName() );
          assertNull( user.getPassword() );

          assertEquals( "Skeppsbron 24", user.getAddress() );
          assertEquals( "VISBY", user.getCity() );
          assertEquals( "Imcode", user.getCompany() );
          assertEquals( "SWEDEN", user.getCountry() );
          assertEquals( "Gotland", user.getCountyCouncil() );
          assertEquals( "kreiger@imcode.com", user.getEmailAddress() );
          assertEquals( "Christoffer", user.getFirstName() );
          assertEquals( "hemtelenummer", user.getHomePhone() );
          assertEquals( "Hammarström", user.getLastName() );
          assertEquals( "Hacker", user.getTitle() );
          assertEquals( "0708 60 89 68", user.getMobilePhone() );
          assertEquals( "0498 200 300", user.getWorkPhone() );
          assertEquals( "621 57", user.getZip() );
          assertTrue( user.isActive() );
          assertNull( user.getLangPrefix() );
       }
    */

    /*
       public void testExistingUserWithUnsetAttributes() {
          User user = ldapUserAndRoleMapper.getUser( "imcms_ldaptest" );
          assertNotNull( user );
          assertEquals( "imcms_ldaptest", user.getLoginName() );
          assertNull( user.getPassword() );

          assertEquals( "", user.getAddress() );
          assertEquals( "", user.getCity() );
          assertEquals( "", user.getCompany() );
          assertEquals( "", user.getCountry() );
          assertEquals( "", user.getCountyCouncil() );
          assertEquals( "imcms_ldaptest", user.getEmailAddress() );
          assertEquals( "imcms_ldaptest", user.getFirstName() );
          assertEquals( "", user.getHomePhone() );
          assertEquals( "imcms_ldaptest", user.getLastName() );
          assertEquals( "", user.getTitle() );
          assertEquals( "", user.getMobilePhone() );
          assertEquals( "", user.getWorkPhone() );
          assertEquals( "", user.getZip() );
          assertTrue( user.isActive() );
          assertNull( user.getLangPrefix() );
       }
    */

    /*
       public void testGetRolesForChristoffer() {
          User user = ldapUserAndRoleMapper.getUser( "chrham" );
          String[] roleNames = ldapUserAndRoleMapper.getRoleNames( user );
          assertNotNull( roleNames );
          assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
       }
    */

    public void testGetAllRoleNames() {
        String[] roleNames = ldapUserAndRoleMapper.getAllRoleNames();
        assertNotNull( roleNames );
        assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
    }

}