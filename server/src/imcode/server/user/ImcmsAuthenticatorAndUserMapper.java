package imcode.server.user;

import imcode.server.IMCServiceInterface;
import org.apache.log4j.Logger;

public class ImcmsAuthenticatorAndUserMapper implements UserMapper, Authenticator {

   private IMCServiceInterface service;
   private final Logger mainLog;
   protected final static String ALWAYS_EXISTING_USERS_ROLE = "Users";
   protected final static String ALWAYS_EXISTING_ADMIN_ROLE = "Superadmin";

   Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserMapper.class ) ;

   public ImcmsAuthenticatorAndUserMapper( IMCServiceInterface service, Logger mainLog ) {
      this.service = service;
      this.mainLog = mainLog;
   }

   public boolean authenticate( String loginName, String password ) {
      boolean userExistsAndPasswordIsCorrect = false;
      User user = getUser( loginName );
      if( null != user ) {
         String login_password_from_db = user.getPassword();
         String login_password_from_form = password;

         if( login_password_from_db.equals( login_password_from_form ) && user.isActive() ) {
            userExistsAndPasswordIsCorrect = true;
         } else if( !user.isActive() ) {
            userExistsAndPasswordIsCorrect = false;
         } else {
            userExistsAndPasswordIsCorrect = false;
         }
      }

      return userExistsAndPasswordIsCorrect;
   }

   public User getUser( String loginName ) {
      loginName = loginName.trim();

      User result = null;
      String[] user_data = service.sqlProcedure( "GetUserByLogin", new String[]{loginName} );

      // if resultSet > 0 a result is found
      if( user_data.length > 0 ) {

         result = staticExtractUserFromStringArray( user_data );

         if( null == result.getLangPrefix() ) {
            result.setLangPrefix( service.getLanguage() );
         }

         String[][] phoneNbr = service.sqlProcedureMulti( "GetUserPhoneNumbers " + user_data[0] );
         String workPhone = "";
         String mobilePhone = "";
         String homePhone = "";

         if( phoneNbr != null ) {
            for( int i = 0; i < phoneNbr.length; i++ ) {
               if( ("2").equals( phoneNbr[i][3] ) ) {
                  workPhone = phoneNbr[i][1];
               } else if( ("3").equals( phoneNbr[i][3] ) ) {
                  mobilePhone = phoneNbr[i][1];
               } else if( ("1").equals( phoneNbr[i][3] ) ) {
                  homePhone = phoneNbr[i][1];
               }
            }
         }
         result.setWorkPhone( workPhone );
         result.setMobilePhone( mobilePhone );
         result.setHomePhone( homePhone );


      } else {
         result = null;
      }

      return result;
   }

   private static User staticExtractUserFromStringArray( String[] user_data ) {
      User result;
      result = new User();

      result.setUserId( Integer.parseInt( user_data[0] ) );
      result.setLoginName( user_data[1] );
      result.setPassword( user_data[2].trim() );
      result.setFirstName( user_data[3] );
      result.setLastName( user_data[4] );
      result.setTitle( user_data[5] );
      result.setCompany( user_data[6] );
      result.setAddress( user_data[7] );
      result.setCity( user_data[8] );
      result.setZip( user_data[9] );
      result.setCountry( user_data[10] );
      result.setCountyCouncil( user_data[11] );
      result.setEmailAddress( user_data[12] );
      result.setLangId( Integer.parseInt( user_data[13] ) );
      result.setUserType( Integer.parseInt( user_data[15] ) );
      result.setActive( 0 != Integer.parseInt( user_data[16] ) );
      result.setCreateDate( user_data[17] );
      result.setLangPrefix( user_data[14] );
      result.setImcmsExternal( 0 != Integer.parseInt( user_data[18] ) );
      return result;
   }

   /**
    @return An object representing the user with the given id.
    **/
   public User getUser( int userId ) {
      String[] user_data = service.sqlProcedure( "GetUserInfo ", new String[]{"" + userId} );
      User result = getUser( user_data[1] );
      return result;
   }


   public void updateUser( String loginName, User newUserData ) {
      String updateUserPRCStr = "UpdateUser";
      User imcmsUser = getUser( loginName );
      User tempUser = (User)newUserData.clone();
      tempUser.setUserId( imcmsUser.getUserId() );
      tempUser.setLoginName( loginName );

      callModifyUserProcedure( updateUserPRCStr, tempUser );
   }

   public synchronized void addUser( User newUser ) {
      String updateUserPRCStr = "AddNewUser";
      String newUserId = service.sqlProcedureStr( "GetHighestUserId" );
      int newIntUserId = Integer.parseInt( newUserId );
      newUser.setUserId( newIntUserId );
      callModifyUserProcedure( updateUserPRCStr, newUser );
   }


   private void callModifyUserProcedure( String modifyUserProcedureName, User tempUser ) {
      String[] params = {String.valueOf( tempUser.getUserId() ), tempUser.getLoginName(), null == tempUser.getPassword() ? "" : tempUser.getPassword(), tempUser.getFirstName(), tempUser.getLastName(), tempUser.getTitle(), tempUser.getCompany(), tempUser.getAddress(), tempUser.getCity(), tempUser.getZip(), tempUser.getCountry(), tempUser.getCountyCouncil(), tempUser.getEmailAddress(), tempUser.isImcmsExternal() ? "1" : "0", "1001", "0", String.valueOf( tempUser.getLangId() ), String.valueOf( tempUser.getUserType() ), tempUser.isActive() ? "1" : "0"};
      service.sqlUpdateProcedure( modifyUserProcedureName, params );
   }

   public String[] getRoleNames( User user ) {
      String[] roleNames = service.sqlProcedure( "GetUserRoles", new String[]{"" + user.getUserId()} );
      return roleNames;
   }

   public String[] getAllRoleNames() {
      String[] roleNamesMinusUsers = service.sqlProcedure( "GetAllRoles" );
      String[] roleNames = new String[roleNamesMinusUsers.length + 1];
      roleNames[0] = ALWAYS_EXISTING_USERS_ROLE;
      for( int i = 0; i < roleNamesMinusUsers.length; i++ ) {
         roleNames[i + 1] = roleNamesMinusUsers[i];
      }
      return roleNames;
   }

   public synchronized void addRole( String roleName ) {
      String[] userId = service.sqlProcedure("RoleFindName", new String[] {roleName}) ;
      boolean roleExists = -1 != Integer.parseInt(userId[0]) ;
      if (!roleExists) {
         service.sqlUpdateProcedure( "RoleAddNew", new String[]{roleName} );
      }
   }

   public void addRoleNames( String[] externalRoleNames ) {
      for( int i = 0; i < externalRoleNames.length; i++ ) {
         String externalRoleName = externalRoleNames[i];
         this.addRole( externalRoleName );
      }
   }

   public void assignRoleToUser( User user, String roleName ) {
      String userIdStr = String.valueOf(user.getUserId()) ;
      log.debug("Trying to assign role "+roleName+" to user "+user.getLoginName()) ;
      String rolesIdStr = service.sqlProcedureStr("GetRoleIdByRoleName", new String[]{roleName});
      service.sqlUpdateProcedure( "AddUserRole", new String[]{ userIdStr, rolesIdStr } ) ;
   }
}