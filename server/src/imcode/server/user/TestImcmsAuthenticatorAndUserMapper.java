package imcode.server.user;

import org.apache.log4j.Logger;

public class TestImcmsAuthenticatorAndUserMapper extends UserBaseTestCase {
   private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
   private MockIMCServiceInterface mockService;

   protected void setUp() throws Exception {
      Logger logger = Logger.getLogger( this.getClass()  );
      mockService = new MockIMCServiceInterface();
      imcmsAAUM = new ImcmsAuthenticatorAndUserMapper(mockService, logger);
   }

   public void testFalseUser() {
      mockService.setExpectedSQLResult( new String[][] {{}} );

      boolean exists = imcmsAAUM.authenticate("aösdlhf","asdöflkjaödfs");
      assertTrue( !exists );
   }

   public void testAdmin() {
      mockService.setExpectedSQLResult( new String[][]{SQL_RESULT_ADMIN} );

      boolean exists = imcmsAAUM.authenticate("admin","admin");
      assertTrue( exists );
   }

   public void testUserUser() {
      mockService.setExpectedSQLResult( new String[][]{ SQL_RESULT_USER, SQL_RESULT_USER } );

      String loginName = "user";
      boolean exists = imcmsAAUM.authenticate(loginName,"user");
      assertTrue( exists );

      User user = imcmsAAUM.getUser(loginName) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(loginName));
   }
}

