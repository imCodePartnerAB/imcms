package imcode.server.user;

import imcode.server.IMCServiceInterface;
import org.apache.log4j.Logger;

public class ImcmsAuthenticatorAndUserMapper implements UserAndRoleMapper, Authenticator {

   // todo: make sure that these stored procedures are accesed (called) only used within this class
   // todo: and nowhere else directly to decouple
   // todo: if not, make the constant public and use it in place?
   // todo: Remove space in constansts
   private static final String SPROC_GET_ALL_USERS = "getAllUsers";
   private static final String SPROC_GET_ROLE_ID_BY_ROLE_NAME = "GetRoleIdByRoleName";
   private static final String SPROC_ADD_USER_ROLE = "AddUserRole";
   private static final String SPROC_ROLE_ADD_NEW = "RoleAddNew";
   private static final String SPROC_ROLE_FIND_NAME = "RoleFindName";
   private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
   private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
   private static final String SPROC_GET_HIGHEST_USER_ID = "GetHighestUserId";
   private static final String SPROC_ADD_NEW_USER = "AddNewUser";
   private static final String SPROC_UPDATE_USER = "UpdateUser";
   private static final String SPROC_GET_USER_INFO = "GetUserInfo ";
   private static final String SPROC_GET_USER_PHONE_NUMBERS = "GetUserPhoneNumbers ";
   private static final String SPROC_GET_USER_BY_LOGIN = "GetUserByLogin";

   private IMCServiceInterface service;
   final static String ALWAYS_EXISTING_USERS_ROLE = "Users";
   final static String ALWAYS_EXISTING_ADMIN_ROLE = "Superadmin";

   private Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserMapper.class );

   public ImcmsAuthenticatorAndUserMapper( IMCServiceInterface service ) {
      this.service = service;
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
      String[] user_data = service.sqlProcedure( SPROC_GET_USER_BY_LOGIN, new String[]{loginName} );

      // if resultSet > 0 a result is found
      if( user_data.length > 0 ) {

         result = staticExtractUserFromStringArray( user_data );

         if( null == result.getLangPrefix() ) {
            result.setLangPrefix( service.getLanguage() );
         }

         String[][] phoneNbr = service.sqlProcedureMulti( SPROC_GET_USER_PHONE_NUMBERS + user_data[0] );
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
      result.setLangPrefix( user_data[14] );
      result.setUserType( Integer.parseInt( user_data[15] ) );
      result.setActive( 0 != Integer.parseInt( user_data[16] ) );
      result.setCreateDate( user_data[17] );
      result.setImcmsExternal( 0 != Integer.parseInt( user_data[18] ) );
      return result;
   }

   /**
    @return An object representing the user with the given id.
    **/
   public User getUser( int userId ) {
      String[] user_data = service.sqlProcedure( SPROC_GET_USER_INFO, new String[]{"" + userId} );
      User result = getUser( user_data[1] );
      return result;
   }


   public void updateUser( String loginName, User newUserData ) {
      String updateUserPRCStr = SPROC_UPDATE_USER;
      User imcmsUser = getUser( loginName );
      User tempUser = (User)newUserData.clone();
      tempUser.setUserId( imcmsUser.getUserId() );
      tempUser.setLoginName( loginName );

      callModifyUserProcedure( updateUserPRCStr, tempUser );
   }

   public synchronized void addUser( User newUser ) {
      String updateUserPRCStr = SPROC_ADD_NEW_USER;
      String newUserId = service.sqlProcedureStr( SPROC_GET_HIGHEST_USER_ID );
      int newIntUserId = Integer.parseInt( newUserId );
      newUser.setUserId( newIntUserId );
      callModifyUserProcedure( updateUserPRCStr, newUser );
   }


   private void callModifyUserProcedure( String modifyUserProcedureName, User tempUser ) {
      String[] params = { String.valueOf( tempUser.getUserId() ),
                          tempUser.getLoginName(),
                          null == tempUser.getPassword() ? "" : tempUser.getPassword(),
                          tempUser.getFirstName(),
                          tempUser.getLastName(),
                          tempUser.getTitle(),
                          tempUser.getCompany(),
                          tempUser.getAddress(),
                          tempUser.getCity(),
                          tempUser.getZip(),
                          tempUser.getCountry(),
                          tempUser.getCountyCouncil(),
                          tempUser.getEmailAddress(),
                          tempUser.isImcmsExternal() ? "1" : "0",
                          "1001",
                          "0",
                          String.valueOf( tempUser.getLangId() ),
                          String.valueOf( tempUser.getUserType() ),
                          tempUser.isActive() ? "1" : "0" };
      service.sqlUpdateProcedure( modifyUserProcedureName, params );
   }

   public String[] getRoleNames( User user ) {
      String[] roleNames = service.sqlProcedure( SPROC_GET_USER_ROLES, new String[]{"" + user.getUserId()} );
      return roleNames;
   }

   public String[] getAllRoleNames() {
      String[] roleNamesMinusUsers = service.sqlProcedure( SPROC_GET_ALL_ROLES );
      String[] roleNames = new String[roleNamesMinusUsers.length + 1];
      roleNames[0] = ALWAYS_EXISTING_USERS_ROLE;
      for( int i = 0; i < roleNamesMinusUsers.length; i++ ) {
         roleNames[i + 1] = roleNamesMinusUsers[i];
      }
      return roleNames;
   }

   public synchronized void addRole( String roleName ) {
      String[] userId = service.sqlProcedure( SPROC_ROLE_FIND_NAME, new String[]{roleName} );
      boolean roleExists = -1 != Integer.parseInt( userId[0] );
      if( !roleExists ) {
         service.sqlUpdateProcedure( SPROC_ROLE_ADD_NEW, new String[]{roleName} );
      }
   }

   public void addRoleNames( String[] externalRoleNames ) {
      for( int i = 0; i < externalRoleNames.length; i++ ) {
         String externalRoleName = externalRoleNames[i];
         this.addRole( externalRoleName );
      }
   }

   public void assignRoleToUser( User user, String roleName ) {
      String userIdStr = String.valueOf( user.getUserId() );
      addRole( roleName );
      log.debug( "Trying to assign role " + roleName + " to user " + user.getLoginName() );
      String rolesIdStr = service.sqlProcedureStr( SPROC_GET_ROLE_ID_BY_ROLE_NAME, new String[]{roleName} );
      service.sqlUpdateProcedure( SPROC_ADD_USER_ROLE, new String[]{userIdStr, rolesIdStr} );
   }

   // todo: make a quicker version that not loops over all of the user_ids and makes a new db searc
   // todo: change the "getAllUsers" sproc to specify its arguments.
   public User[] getAllUsers() {
      int noOfColumnsInSearchResult = 20;
      String[] allUsersSqlResult = service.sqlProcedure( SPROC_GET_ALL_USERS );
      int noOfUsers = allUsersSqlResult.length / noOfColumnsInSearchResult;
      User[] result = new User[noOfUsers];
      for( int i = 0; i < noOfUsers; i++ ) {
         String userId = allUsersSqlResult[i * noOfColumnsInSearchResult];
         result[i] = getUser( Integer.parseInt(userId) );
      }
      return result;
   }
}