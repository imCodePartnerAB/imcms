package imcode.server.user;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class TestExternalizedImcmsAuthenticatorAndUserMapper extends UserBaseTestCase {
   private ExternalizedImcmsAuthenticatorAndUserMapper imcmsAndLdapAuthAndMapper;
   private MockIMCServiceInterface mockImcmsService;
   protected static final String LOGIN_NAME_HASBRA = "hasbra";
   protected static final String LOGIN_NAME_ADMIN = "admin";

   public void testDummy() {
      assertTrue( true );
   }

   public void setUp()  throws LdapUserMapper.LdapInitException  {
      Logger logger = Logger.getLogger( this.getClass() );
      mockImcmsService = new MockIMCServiceInterface();
      String ldapServerURL = "ldap://loke:389/CN=Users,DC=imcode,DC=com";
      String ldapAuthenticationType = "simple";
      String ldapUserName = "imcode\\hasbra";
      String ldapPassword = "hasbra";
      LdapUserMapper ldapUserMapper = new LdapUserMapper( ldapServerURL,
                                                          ldapAuthenticationType,
                                                          ldapUserName,
                                                          ldapPassword,
                                                          new String[0]);
      imcmsAndLdapAuthAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper(
         new ImcmsAuthenticatorAndUserMapper( mockImcmsService, logger ),
         new SmbAuthenticator(),
         ldapUserMapper,
         "se");
   }

   public void testImcmsOnlyExisting(){
      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_ADMIN } );

      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate(LOGIN_NAME_ADMIN, LOGIN_NAME_ADMIN );
      assertTrue( userAuthenticates );

      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_ADMIN } );
      User user = imcmsAndLdapAuthAndMapper.getUser(LOGIN_NAME_ADMIN) ;

      assertNotNull(user) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(LOGIN_NAME_ADMIN));
   }

   public void testLdapOnlyAuthentication() {
      mockImcmsService.setExpectedSQLResult( new String[][]{{}} );
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( LOGIN_NAME_HASBRA, LOGIN_NAME_HASBRA );

      assertTrue( userAuthenticates );
   }

   public void testLdapOnlyExisting() {
      mockImcmsService.setExpectedSQLResult( new String[][]{{},SQL_RESULT_HASBRA} );
      User user = imcmsAndLdapAuthAndMapper.getUser(LOGIN_NAME_HASBRA) ;

      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }

   public void testLdapAndImcmsUpdateSynchronization() {
      mockImcmsService.setExpectedSQLResult( new String[][]{SQL_RESULT_HASBRA,
                                                            SQL_RESULT_HASBRA,
                                                            SQL_RESULT_HASBRA} );
      User user = imcmsAndLdapAuthAndMapper.getUser(LOGIN_NAME_HASBRA) ;

      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }

   public void testAlwaysExistingImcmsRole() {
      String[][] expectedSqlResult = new String[][]{SQL_RESULT_HASBRA,
                                                            SQL_RESULT_HASBRA,
                                                            SQL_RESULT_HASBRA,
                                                            new String[] {ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE}} ;
      mockImcmsService.setExpectedSQLResult( expectedSqlResult );
      User user = imcmsAndLdapAuthAndMapper.getUser(LOGIN_NAME_HASBRA) ;
      String[] userRoles = imcmsAndLdapAuthAndMapper.getRoleNames(user) ;

      assertTrue( Arrays.asList(userRoles).contains(ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE) ) ;
      assertTrue( Arrays.asList( userRoles ).contains( LdapUserMapper.DEFAULT_LDAP_ROLE ) );
   }

   public void testGetRoles() {
      final String[][] expectedSQLResult = new String[][] { { "0", "Superadmin", "1", "Useradmin" } };
      mockImcmsService.setExpectedSQLResult( expectedSQLResult ) ;
      String[] roles = imcmsAndLdapAuthAndMapper.getRoles() ;
      assertTrue( Arrays.asList(roles).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE )) ;
      assertTrue( Arrays.asList(roles).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_ADMIN_ROLE )) ;
   }


   public void testAddRoleFromOtherIntoImcms() {
     imcmsAndLdapAuthAndMapper.getUser( LOGIN_NAME_HASBRA );
     String[] roles = imcmsAndLdapAuthAndMapper.getRoles();
     assertTrue( Arrays.asList( roles ).contains( LdapUserMapper.DEFAULT_LDAP_ROLE ));
   }

}
