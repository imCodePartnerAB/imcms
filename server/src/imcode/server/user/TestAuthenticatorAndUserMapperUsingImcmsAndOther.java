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
      imcmsAndLdapAuthAndMapper = new AuthenticatorAndUserMapperUsingImcmsAndOther(
         new ImcmsAuthenticatorAndUserMapper( mockImcmsService, logger ),
         new LdapAuthenticator(),
         new LdapUserMapper() );
   }

   public void testImcmsOnlyExisting(){
      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_ADMIN } );

      String loginName = "admin";
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( loginName, "admin" );
      assertTrue( userAuthenticates );

      imcode.server.user.User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
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
      imcode.server.user.User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }

   public void testLdapAndImcmsUpdateSynchronization() {
      mockImcmsService.setExpectedSQLResult( new String[][]{SQL_RESULT_HASBRA,SQL_RESULT_HASBRA,SQL_RESULT_HASBRA} );
      String loginName = "hasbra";
      imcode.server.user.User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }
}
