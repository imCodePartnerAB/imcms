package imcode.server.user;

import org.apache.log4j.Logger;

import java.util.Arrays;

import com.imcode.imcms.api.*;

public class TestImcmsAuthenticatorAndUserMapper extends UserBaseTestCase {
   private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
   private MockIMCServiceInterface mockImcmsService;

   protected void setUp() throws Exception {
      Logger logger = Logger.getLogger( this.getClass() );
      mockImcmsService = new MockIMCServiceInterface();
      imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( mockImcmsService );
   }

   public void testFalseUser() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, new String[]{} );

      boolean exists = imcmsAAUM.authenticate( "aösdlhf", "asdöflkjaödfs" );
      assertTrue( !exists );
      mockImcmsService.verify();
   }

   public void testAdmin() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );

      boolean exists = imcmsAAUM.authenticate( "admin", "admin" );
      assertTrue( exists );
      mockImcmsService.verify();
   }

   public void testUserUser() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_USER );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_USER );

      String loginName = "user";
      boolean exists = imcmsAAUM.authenticate( loginName, "user" );
      assertTrue( exists );

      UserDomainObject user = imcmsAAUM.getUser( loginName );
      assertTrue( user.getFirstName().equalsIgnoreCase( loginName ) );
      mockImcmsService.verify();
   }

   public void testUserRoleUsers() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_USER );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERROLES, new String[]{RoleConstants.USERS} );

      String loginName = "user";
      UserDomainObject user = imcmsAAUM.getUser( loginName );
      String[] roleNames = imcmsAAUM.getRoleNames( user );
      assertTrue( Arrays.asList( roleNames ).contains( RoleConstants.USERS ) );
      mockImcmsService.verify();
   }

   public void testGetRoles() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETALLROLES, new String[]{"0", "Superadmin", "1", "Useradmin"} );

      String[] roles = imcmsAAUM.getAllRoleNames();
      assertTrue( Arrays.asList( roles ).contains( RoleConstants.USERS) );
      assertTrue( Arrays.asList( roles ).contains( RoleConstants.SUPER_ADMIN ) );
      mockImcmsService.verify();
   }

   public void testAddRole() {
      String roleName = "testrole";
      final String[] expectedSQLResult = new String[]{"0", "Superadmin", "1", "Useradmin", "3", roleName};
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, expectedSQLResult );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETALLROLES, expectedSQLResult );

      imcmsAAUM.addRole( roleName );
      String[] roles = imcmsAAUM.getAllRoleNames();
      assertTrue( Arrays.asList( roles ).contains( roleName ) );
      mockImcmsService.verify();
   }
}
