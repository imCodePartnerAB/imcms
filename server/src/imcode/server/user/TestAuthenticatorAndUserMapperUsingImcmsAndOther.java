package imcode.server.user;

import org.apache.log4j.Logger;

public class TestAuthenticatorAndUserMapperUsingImcmsAndOther extends UserBaseTestCase {
   private AuthenticatorAndUserMapperUsingImcmsAndOther imcmsAndLdapAuthAndMapper;
   private MockIMCServiceInterface mockImcmsService;

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
                                                          "se");
      imcmsAndLdapAuthAndMapper = new AuthenticatorAndUserMapperUsingImcmsAndOther(
         new ImcmsAuthenticatorAndUserMapper( mockImcmsService, logger ),
         new SmbAuthenticator(),
         ldapUserMapper );
   }

   public void testImcmsOnlyExisting(){
      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_ADMIN } );

      String loginName = "admin";
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( loginName, "admin" );
      assertTrue( userAuthenticates );

      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_ADMIN } );
      User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertNotNull(user) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(loginName));
   }

   public void testLdapOnlyAuthentication() {
      mockImcmsService.setExpectedSQLResult( new String[][]{{}} );
      String loginName = "hasbra";
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( loginName, "hasbra" );
      assertTrue( userAuthenticates );
   }

   public void testLdapOnlyExisting() {
      mockImcmsService.setExpectedSQLResult( new String[][]{{},SQL_RESULT_HASBRA} );
      String loginName = "hasbra";
      User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }

   public void testLdapAndImcmsUpdateSynchronization() {
      mockImcmsService.setExpectedSQLResult( new String[][]{SQL_RESULT_HASBRA,SQL_RESULT_HASBRA,SQL_RESULT_HASBRA} );
      String loginName = "hasbra";
      User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }
}
