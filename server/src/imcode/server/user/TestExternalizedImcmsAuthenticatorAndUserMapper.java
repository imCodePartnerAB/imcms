package imcode.server.user;

import com.imcode.imcms.Role;

import java.util.Arrays;

public class TestExternalizedImcmsAuthenticatorAndUserMapper extends UserBaseTestCase {
   private ExternalizedImcmsAuthenticatorAndUserMapper externalizedImcmsAndUserMapper;
   private MockIMCServiceInterface mockImcmsService;
   private ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper;
   private LdapUserAndRoleMapper ldapUserMapper;

   public void testDummy() {
      assertTrue( true );
   }

   public void setUp() throws LdapUserAndRoleMapper.LdapInitException {
      mockImcmsService = new MockIMCServiceInterface();
//      String ldapURL = "ldap://ldap-vcn1.vtd.volvo.se:389/dc=vcn,dc=ds,dc=volvo,dc=net";
//      String ldapUserIdentifyingAttribute = "cn";
//      String ldapUserName = "CN=cs-ad-ldapquery,OU=ServiceAccounts,OU=AdOperation,OU=CS,DC=vcn,DC=ds,DC=volvo,DC=net";
//      String ldapPassword = "#D8leYS";

      String ldapURL = "ldap://loke:389/CN=Users,DC=imcode,DC=com";
      String ldapUserObjectClass = "person";
      String ldapUserIdentifyingAttribute = "samaccountname";
      String ldapUserName = "imcode\\hasbra";
      String ldapPassword = "hasbra";
      ldapUserMapper = new LdapUserAndRoleMapper( ldapURL, LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE, ldapUserObjectClass, ldapUserIdentifyingAttribute, ldapUserName, ldapPassword, new String[]{LdapUserAndRoleMapper.NONSTANDARD_COMPANY} );
      imcmsAuthenticatorAndUserMapper = new ImcmsAuthenticatorAndUserMapper( mockImcmsService );
      externalizedImcmsAndUserMapper = new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, ldapUserMapper, ldapUserMapper, "se" );
   }

   public void testImcmsOnlyExisting() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );

      boolean userAuthenticates = externalizedImcmsAndUserMapper.authenticate( LOGIN_NAME_ADMIN, LOGIN_NAME_ADMIN );
      assertTrue( userAuthenticates );

      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );
      User user = externalizedImcmsAndUserMapper.getUser( LOGIN_NAME_ADMIN );

      assertNotNull( user );
      assertTrue( user.getFirstName().equalsIgnoreCase( LOGIN_NAME_ADMIN ) );
      mockImcmsService.verify();
   }

   public void testLdapOnlyAuthentication() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, new String[]{} );
      boolean userAuthenticates = externalizedImcmsAndUserMapper.authenticate( LOGIN_NAME_HASBRA, LOGIN_NAME_HASBRA );

      assertTrue( userAuthenticates );
      mockImcmsService.verify();
   }

   public void testLdapOnlyExisting() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, new String[]{} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"2"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );

      User user = externalizedImcmsAndUserMapper.getUser( LOGIN_NAME_HASBRA );

      assertTrue( "hasse".equalsIgnoreCase( user.getFirstName() ) );
      mockImcmsService.verify();
   }

   public void testLdapAndImcmsUpdateSynchronization() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"2"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );

      User user = externalizedImcmsAndUserMapper.getUser( LOGIN_NAME_HASBRA );

      assertTrue( "hasse".equalsIgnoreCase( user.getFirstName() ) );
      mockImcmsService.verify();
   }

   public void testMergedRolesFromImcmsAndExternal() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_HASBRA );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"2"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERROLES, new String[]{Role.USERS} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERROLES, new String[]{Role.USERS} );

      User user = externalizedImcmsAndUserMapper.getUser( LOGIN_NAME_HASBRA );
      String[] userRoles = externalizedImcmsAndUserMapper.getRoleNames( user );

      assertTrue( Arrays.asList( userRoles ).contains( Role.USERS ) );
      assertTrue( Arrays.asList( userRoles ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
      assertTrue( Arrays.asList( userRoles ).contains( "Crisp" ) );

      assertFalse( Arrays.asList( userRoles ).contains( null ) );
      mockImcmsService.verify();
   }

   public void testAddRoleFromExternalIntoImcms() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_ROLEFINDNAME, new String[]{"-1"} );
      mockImcmsService.addExpectedSQLUpdateProcedureCall( SPROC_ROLEADDNEW );
      externalizedImcmsAndUserMapper.synchRolesWithExternal();
      mockImcmsService.verify();
   }

   public void testGetRoles() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETALLROLES, new String[]{"0", "Superadmin", "1", "Useradmin"} );
      String[] roles = externalizedImcmsAndUserMapper.getAllRoleNames();
      assertTrue( Arrays.asList( roles ).contains( Role.USERS ) );
      assertTrue( Arrays.asList( roles ).contains( Role.SUPERADMIN ) );
      assertTrue( Arrays.asList( roles ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
      mockImcmsService.verify();
   }

   public void testNullExternalAuthenticator() {
      try {
         new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, null, ldapUserMapper, "se" );
         fail();
      } catch( IllegalArgumentException ex ) {
         //OK
      }
   }

   public void testNullExternalUserMapper() {
      try {
         new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, ldapUserMapper, null, "se" );
         fail();
      } catch( IllegalArgumentException ex ) {
         //OK
      }
   }

   public void testNullAuthenticatorAndNullUserMapper() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );
      ExternalizedImcmsAuthenticatorAndUserMapper authAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper, null, null, "se" );
      authAndMapper.authenticate( LOGIN_NAME_ADMIN, LOGIN_NAME_ADMIN );
      authAndMapper.getUser( LOGIN_NAME_ADMIN );
      authAndMapper.synchRolesWithExternal();
   }
}
