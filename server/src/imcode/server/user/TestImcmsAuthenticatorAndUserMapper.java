package imcode.server.user;

import org.apache.log4j.Logger;

import java.util.Arrays;

public class TestImcmsAuthenticatorAndUserMapper extends UserBaseTestCase {
   private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
   private MockIMCServiceInterface mockImcmsService;

   protected void setUp() throws Exception {
      Logger logger = Logger.getLogger( this.getClass()  );
      mockImcmsService = new MockIMCServiceInterface();
      imcmsAAUM = new ImcmsAuthenticatorAndUserMapper(mockImcmsService, logger);
   }

   public void testFalseUser() {
      mockImcmsService.setExpectedSQLResult( new String[][] {{}} );

      boolean exists = imcmsAAUM.authenticate("aösdlhf","asdöflkjaödfs");
      assertTrue( !exists );
   }

   public void testAdmin() {
      mockImcmsService.setExpectedSQLResult( new String[][]{SQL_RESULT_ADMIN} );

      boolean exists = imcmsAAUM.authenticate("admin","admin");
      assertTrue( exists );
   }

   public void testUserUser() {
      mockImcmsService.setExpectedSQLResult( new String[][]{ SQL_RESULT_USER, SQL_RESULT_USER } );

      String loginName = "user";
      boolean exists = imcmsAAUM.authenticate(loginName,"user");
      assertTrue( exists );

      User user = imcmsAAUM.getUser(loginName) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(loginName));
   }

   public void testUserRoleUsers() {
      final String[] EXPECTED_ROLES = new String[] { ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE } ;
      mockImcmsService.setExpectedSQLResult( new String[][] {SQL_RESULT_USER, EXPECTED_ROLES} ) ;
      String loginName = "user";
      User user = imcmsAAUM.getUser(loginName) ;
      String[] roleNames = imcmsAAUM.getRoleNames(user) ;
      assertTrue( Arrays.asList( roleNames ).contains(EXPECTED_ROLES[0])) ;
   }

   public void testGetRoles() {
      final String[][] expectedSQLResult = new String[][] { { "0", "Superadmin", "1", "Useradmin" } };
      mockImcmsService.setExpectedSQLResult( expectedSQLResult ) ;
      String[] roles = imcmsAAUM.getRoles() ;
      assertTrue( Arrays.asList(roles).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE )) ;
      assertTrue( Arrays.asList(roles).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_ADMIN_ROLE )) ;
   }

   public void testAddRole() {
      String roleName = "testrole" ;
      final String[][] expectedSQLResult = new String[][] { { "0", "Superadmin",
                                                              "1", "Useradmin",
                                                              "3", roleName } };
      mockImcmsService.setExpectedSQLResult( expectedSQLResult ) ;

      imcmsAAUM.addRole(roleName) ;
      String[] roles = imcmsAAUM.getRoles() ;
      assertTrue( Arrays.asList(roles).contains( roleName )) ;
   }
}

