package imcode.server.user;

import java.util.Arrays;


public class TestLdapUserMapper extends TestUserBaseTestCase {
   private LdapUserAndRoleMapper ldapUserMapper;

   public void setUp() {
      try {

         String ldapURL = "ldap://loke:389/CN=Users,DC=imcode,DC=com" ;
         String ldapUserName = "imcode\\hasbra" ;
         String ldapPassword = "hasbra";
         String ldapUserObjectClass = "person" ;
         String ldapUserIdentifyingAttribute = "samaccountname" ;
         ldapUserMapper = new LdapUserAndRoleMapper(  ldapURL,
                                                      LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE,
                                                      ldapUserObjectClass,
                                                      ldapUserIdentifyingAttribute,
                                                      ldapUserName,
                                                      ldapPassword,
                                                      new String[0] );
      } catch( LdapUserAndRoleMapper.LdapInitException e ) {
         System.err.println(e);
         fail();
      }
   }

   public void testExistingUserLdapService() {
      final String userName = "hasbra" ;
      UserDomainObject user = ldapUserMapper.getUser( userName );
      assertNotNull( user );
      assertEquals( userName, user.getLoginName() );
      assertNull( user.getPassword() );
   }
   
   public void testAuthenticate() {
      final String userName = "hasbra" ;
      final String userPassword = "hasbra" ;

      boolean userAuthenticates = ldapUserMapper.authenticate(userName,userPassword) ;
      assertTrue(userAuthenticates) ;    
   }

   public void testInvalidName() {
      UserDomainObject user = ldapUserMapper.getUser( "" );
      assertNull( user );
   }

   public void testNonExistingUser() {
      UserDomainObject user = ldapUserMapper.getUser( "kalle banan som inte finns" );
      assertNull( user );
   }

   public void testGetRolesForUserLdapService() {
      final String userName = "hasbra" ;
      UserDomainObject user = ldapUserMapper.getUser( userName );
      String[] roleNames = ldapUserMapper.getRoleNames( user );
      assertNotNull( roleNames );
      assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
   }
   
/*
   public void testExistingUserChristoffer() {
      User user = ldapUserMapper.getUser( "chrham" );
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
      User user = ldapUserMapper.getUser( "imcms_ldaptest" );
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
      User user = ldapUserMapper.getUser( "chrham" );
      String[] roleNames = ldapUserMapper.getRoleNames( user );
      assertNotNull( roleNames );
      assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
   }
*/

   public void testGetAllRoleNames() {
      String[] roleNames = ldapUserMapper.getAllRoleNames() ;
      assertNotNull(roleNames) ;
      assertTrue( Arrays.asList( roleNames ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE )) ;
   }

}