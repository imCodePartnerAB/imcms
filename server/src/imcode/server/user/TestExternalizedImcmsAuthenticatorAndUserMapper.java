package imcode.server.user;

import org.apache.log4j.Logger;

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
      Logger logger = Logger.getLogger( this.getClass() );
      mockImcmsService = new MockIMCServiceInterface();
      String ldapServerURL = "ldap://loke:389/CN=Users,DC=imcode,DC=com";
      String ldapUserName = "imcode\\hasbra";
      String ldapPassword = "hasbra";
      ldapUserMapper = new LdapUserAndRoleMapper( ldapServerURL,
                                                  LdapUserAndRoleMapper.AUTHENTICATION_TYPE_SIMPLE,
                                                  ldapUserName,
                                                  ldapPassword,
                                                  new String[]{LdapUserAndRoleMapper.NONSTANDARD_COMPANY} );
      imcmsAuthenticatorAndUserMapper = new ImcmsAuthenticatorAndUserMapper( mockImcmsService );
      externalizedImcmsAndUserMapper = new ExternalizedImcmsAuthenticatorAndUserMapper( imcmsAuthenticatorAndUserMapper,
                                                                                        new SmbAuthenticator( "loke", "imcode" ),
                                                                                        ldapUserMapper,
                                                                                        "se" );
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
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERROLES, new String[]{ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE} );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERROLES, new String[]{ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE} );

      User user = externalizedImcmsAndUserMapper.getUser( LOGIN_NAME_HASBRA );
      String[] userRoles = externalizedImcmsAndUserMapper.getRoleNames( user );

      assertTrue( Arrays.asList( userRoles ).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE ) );
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
      assertTrue( Arrays.asList( roles ).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_USERS_ROLE ) );
      assertTrue( Arrays.asList( roles ).contains( ImcmsAuthenticatorAndUserMapper.ALWAYS_EXISTING_ADMIN_ROLE ) );
      assertTrue( Arrays.asList( roles ).contains( LdapUserAndRoleMapper.DEFAULT_LDAP_ROLE ) );
      mockImcmsService.verify();
   }

   public void testNullExternalAuthenticator() {
      try {
         ExternalizedImcmsAuthenticatorAndUserMapper authAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper(imcmsAuthenticatorAndUserMapper,null,ldapUserMapper,"se") ;
         fail();
      } catch ( IllegalArgumentException ex ) {
         //OK
      }
   }

   public void testNullExternalUserMapper() {
      try {
         ExternalizedImcmsAuthenticatorAndUserMapper authAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper(imcmsAuthenticatorAndUserMapper,new SmbAuthenticator("loke", "imcode"),null,"se") ;
         fail();
      } catch ( IllegalArgumentException ex ) {
         //OK
      }
   }

   public void testNullAuthenticatorAndNullUserMapper() {
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );
      mockImcmsService.addExpectedSQLProcedureCall( SPROC_GETUSERBYLOGIN, SQL_RESULT_ADMIN );
      ExternalizedImcmsAuthenticatorAndUserMapper authAndMapper = new ExternalizedImcmsAuthenticatorAndUserMapper(imcmsAuthenticatorAndUserMapper,null,null,"se") ;
      authAndMapper.authenticate(LOGIN_NAME_ADMIN,LOGIN_NAME_ADMIN) ;
      authAndMapper.getUser( LOGIN_NAME_ADMIN ) ;
      authAndMapper.synchRolesWithExternal();
   }
 }
