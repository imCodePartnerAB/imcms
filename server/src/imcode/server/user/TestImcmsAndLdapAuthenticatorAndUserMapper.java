package imcode.server.user;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import imcode.server.IMCServiceInterface;

public class TestImcmsAndLdapAuthenticatorAndUserMapper extends Log4JInitTestCase {
   private AuthenticatorAndUserMapperUsingImcmsAndOther imcmsAndLdapAuthAndMapper;
   private IMCServiceInterface service;

   protected void setUp() throws Exception {
      Logger logger = Logger.getLogger( this.getClass() );
      service = new MockIMCServiceInterface();
      imcmsAndLdapAuthAndMapper = new AuthenticatorAndUserMapperUsingImcmsAndOther(
         new ImcmsAuthenticatorAndUserMapper( service, logger ),
         new LdapUserMapper() );
   }

   public void testDummy() {
      assertTrue( true );
   }

   public void testImcmsOnlyExisting() {
      String loginName = "admin";
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( loginName, "admin" );
      assertTrue( userAuthenticates );

      imcode.server.user.User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertNotNull(user) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(loginName));
   }

   public void testLdapOnlyExisting() {
      String loginName = "hasbra";
      boolean userAuthenticates = imcmsAndLdapAuthAndMapper.authenticate( loginName, "hasbra" );
      assertTrue( userAuthenticates );

      imcode.server.user.User user = imcmsAndLdapAuthAndMapper.getUser(loginName) ;
      assertTrue( "hasse".equalsIgnoreCase(user.getFirstName()));
   }
}
