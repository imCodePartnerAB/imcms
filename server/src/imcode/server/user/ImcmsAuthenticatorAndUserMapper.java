package imcode.server.user;

import imcode.server.IMCServiceInterface;
import imcode.server.db.DBConnect;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.RoleConstants;

import java.util.*;

public class ImcmsAuthenticatorAndUserMapper implements UserAndRoleMapper, Authenticator {

   // todo: make sure that these stored procedures are accesed (called) only used within this class
   // todo: and nowhere else directly to decouple
   // todo: if not, make the constant public and use it in place?
   // todo: Remove space in constansts
   private static final String SPROC_GET_ALL_USERS = "getAllUsers";
   private static final String SPROC_GET_HIGHEST_USER_ID = "GetHighestUserId";
   private static final String SPROC_ADD_NEW_USER = "AddNewUser";
   private static final String SPROC_UPDATE_USER = "UpdateUser";
   private static final String SPROC_GET_USER_INFO = "GetUserInfo ";
   private static final String SPROC_GET_USER_BY_LOGIN = "GetUserByLogin";

   private static final String SPROC_GET_ROLE_ID_BY_ROLE_NAME = "GetRoleIdByRoleName";
   private static final String SPROC_ADD_USER_ROLE = "AddUserRole";
   private static final String SPROC_ROLE_ADD_NEW = "RoleAddNew";
   private static final String SPROC_ROLE_DELETE = "RoleDelete";
   private static final String SPROC_ROLE_FIND_NAME = "RoleFindName";
   private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
   private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
   private static final String SPROC_DEL_USER_ROLES = "DelUserRoles" ;
   private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

   private static final String SPROC_GET_USER_PHONE_NUMBERS = "GetUserPhoneNumbers ";
   private static final String SPROC_PHONE_NBR_ADD = "phoneNbrAdd";
   private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

   private IMCServiceInterface service;
   private Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserMapper.class );

    public ImcmsAuthenticatorAndUserMapper( IMCServiceInterface service ) {
      this.service = service;
   }

   public boolean authenticate( String loginName, String password ) {
      boolean userExistsAndPasswordIsCorrect = false;
      UserDomainObject user = getUser( loginName );
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

   public UserDomainObject getUser( String loginName ) {
      loginName = loginName.trim();

      UserDomainObject user = null;
      String[] user_data = service.sqlProcedure( SPROC_GET_USER_BY_LOGIN, new String[]{loginName} );

      // if resultSet > 0 a result is found
      if( user_data.length > 0 ) {

         user = staticExtractUserFromStringArray( user_data );

         if( null == user.getLangPrefix() ) {
            user.setLangPrefix( service.getDefaultLanguageAsIso639_1() );
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
         user.setWorkPhone( workPhone );
         user.setMobilePhone( mobilePhone );
         user.setHomePhone( homePhone );


      } else {
         user = null;
      }

      return user;
   }

   private static UserDomainObject staticExtractUserFromStringArray( String[] user_data ) {
      UserDomainObject result;
      result = new UserDomainObject();

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
   public UserDomainObject getUser( int userId ) {
      String[] user_data = service.sqlProcedure( SPROC_GET_USER_INFO, new String[]{"" + userId} );
      UserDomainObject result = getUser( user_data[1] );
      return result;
   }


   public void updateUser( String loginName, UserDomainObject newUser ) {
      String updateUserPRCStr = SPROC_UPDATE_USER;
      UserDomainObject imcmsUser = getUser( loginName );
      UserDomainObject tempUser = (UserDomainObject)newUser.clone();
      tempUser.setUserId( imcmsUser.getUserId() );
      tempUser.setLoginName( loginName );

      callSprocModifyUserProcedure( updateUserPRCStr, tempUser );
      removePhoneNumbers( tempUser );
      addPhonenNmbers( tempUser );
   }

   public synchronized void addUser( UserDomainObject newUser ) {
      String updateUserPRCStr = SPROC_ADD_NEW_USER;
      String newUserId = service.sqlProcedureStr( SPROC_GET_HIGHEST_USER_ID );
      int newIntUserId = Integer.parseInt( newUserId );
      newUser.setUserId( newIntUserId );

      callSprocModifyUserProcedure( updateUserPRCStr, newUser );
      addPhonenNmbers( newUser );
   }

   private void removePhoneNumbers( UserDomainObject newUser ) {
      staticSprocDelPhoneNr( service, newUser.getUserId() );
   }

   private void addPhonenNmbers( UserDomainObject newUser ) {
      final int PHONE_TYPE_HOME_PHONE = 1;
      final int PHONE_TYPE_WORK_PHONE = 2;
      final int PHONE_TYPE_WORK_MOBILE = 3;
      staticSprocPhoneNbrAdd( service, newUser.getUserId(), newUser.getHomePhone(), PHONE_TYPE_HOME_PHONE );
      staticSprocPhoneNbrAdd( service, newUser.getUserId(), newUser.getWorkPhone(), PHONE_TYPE_WORK_PHONE );
      staticSprocPhoneNbrAdd( service, newUser.getUserId(), newUser.getMobilePhone(), PHONE_TYPE_WORK_MOBILE );
   }

   public String[] getRoleNames( UserDomainObject user ) {
      String[] roleNames = service.sqlProcedure( SPROC_GET_USER_ROLES, new String[]{"" + user.getUserId()} );
      return roleNames;
   }

   public String[] getAllRoleNames() {
      String[] roleNamesMinusUsers = service.sqlProcedure( SPROC_GET_ALL_ROLES );

      Set roleNamesSet = new HashSet() ;
      for( int i = 0; i < roleNamesMinusUsers.length; i+=2 ) {
         String roleName = roleNamesMinusUsers[i+1] ;
         roleNamesSet.add(roleName) ;
      }

      roleNamesSet.add(RoleConstants.USERS) ;

      String[] roleNames = (String[])roleNamesSet.toArray(new String[roleNamesSet.size()]);
      return roleNames;
   }

    public RoleDomainObject getRole( String roleName ) {
        String sqlResult = service.sqlProcedureStr(SPROC_GET_ROLE_ID_BY_ROLE_NAME, new String [] { roleName });
        if ( null != sqlResult ) {
            return new RoleDomainObject( Integer.parseInt(sqlResult), roleName );
        }
        return null ;
    }

    public void addRoleNames( String[] externalRoleNames ) {
      for( int i = 0; i < externalRoleNames.length; i++ ) {
         String externalRoleName = externalRoleNames[i];
         this.addRole( externalRoleName );
      }
   }

   public void addRoleToUser( UserDomainObject user, String roleName ) {
      String userIdStr = String.valueOf( user.getUserId() );
      addRole( roleName );
      log.debug( "Trying to assign role " + roleName + " to user " + user.getLoginName() );
      String rolesIdStr = staticSprocCallGetRoleIdByRoleName( service, roleName );
      service.sqlUpdateProcedure( SPROC_ADD_USER_ROLE, new String[]{userIdStr, rolesIdStr} );
   }

   // todo: make a quicker version that not loops over all of the user_ids and makes a new db searc
   // todo: change the "getAllUsers" sproc to specify its arguments.
   public UserDomainObject[] getAllUsers() {
      int noOfColumnsInSearchResult = 20;
      String[] allUsersSqlResult = service.sqlProcedure( SPROC_GET_ALL_USERS );
      int noOfUsers = allUsersSqlResult.length / noOfColumnsInSearchResult;
      UserDomainObject[] result = new UserDomainObject[noOfUsers];
      for( int i = 0; i < noOfUsers; i++ ) {
         String userId = allUsersSqlResult[i * noOfColumnsInSearchResult];
         result[i] = getUser( Integer.parseInt(userId) );
      }
      return result;
   }

   public void setUserRoles( UserDomainObject user, String[] roleNames ) {
      this.removeAllRoles( user );

      for( int i = 0; i < roleNames.length; i++ ) {
         String roleName = roleNames[i];
         this.addRoleToUser( user, roleName );
      }
   }

   private void removeAllRoles( UserDomainObject user ) {
      service.sqlUpdateProcedure( SPROC_DEL_USER_ROLES, new String[] { ""+user.getUserId(), "-1" }) ;
   }

   public UserDomainObject[] getAllUsersWithRole( String roleName ) {
      String rolesIdStr = staticSprocCallGetRoleIdByRoleName( service, roleName );
      String[] usersWithRole = service.sqlProcedure(SPROC_GET_USERS_WHO_BELONGS_TO_ROLE, new String[] {rolesIdStr} );
      UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

      for( int i = 0; i < result.length; i++ ) {
         String userIdStr = usersWithRole[i*2];
         UserDomainObject user = getUser(Integer.parseInt(userIdStr)) ;
         result[i] = user ;
      }
      return result;
   }

   private static String staticSprocCallGetRoleIdByRoleName( IMCServiceInterface service,
                                                             String roleName ) {
      String rolesIdStr = service.sqlProcedureStr( SPROC_GET_ROLE_ID_BY_ROLE_NAME, new String[]{roleName} );
      return rolesIdStr;
   }

   public static void staticSprocPhoneNbrAdd( IMCServiceInterface service,
                                              int newUserId, String phoneNumber, int phoneNumberType ) {
      String[] sprocParameters = new String[]{ String.valueOf(newUserId), phoneNumber, String.valueOf(phoneNumberType) };
      service.sqlUpdateProcedure( SPROC_PHONE_NBR_ADD, sprocParameters );
   }

   private static void staticSprocDelPhoneNr( IMCServiceInterface service, int userId ) {
      String[] sprocParameters = new String[] { String.valueOf(userId) };
      service.sqlUpdateProcedure( SPROC_DEL_PHONE_NR, sprocParameters );
   }

    public boolean hasSuperAdminRole( UserDomainObject user ) {
        String[] userRoleNames = this.getRoleNames( user );
        boolean userHasSuperAdminRole = Arrays.asList( userRoleNames ).contains( RoleConstants.SUPER_ADMIN );
        return userHasSuperAdminRole;
    }

    public synchronized void addRole( String roleName ) {
        String[] roleId = callSprocRoleFindName( roleName );
        boolean roleExists = -1 != Integer.parseInt( roleId[0] );
        if( !roleExists ) {
          service.sqlUpdateProcedure( SPROC_ROLE_ADD_NEW, new String[]{roleName} );
       }
    }

    public void deleteRole( String roleName ) {
        String[] roleId = callSprocRoleFindName( roleName );
        boolean roleExists = -1 != Integer.parseInt( roleId[0] );
        if( roleExists ) {
            service.sqlUpdateProcedure( SPROC_ROLE_DELETE, roleId );
        }
    }

    /**
     *
     * @param roleName
     * @return roleId
     */
    private String[] callSprocRoleFindName( String roleName ) {
        String[] params = new String[]{roleName};
        String[] userId = service.sqlProcedure( SPROC_ROLE_FIND_NAME, params );
        return userId;
    }

    private void callSprocModifyUserProcedure( String modifyUserProcedureName, UserDomainObject tempUser ) {
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

    public static Vector sprocGetUserPermissionSet( DBConnect dbc, String meta_id_str, String user_id_str ) {
        dbc.setProcedure( "GetUserPermissionSet", new String[]{meta_id_str, user_id_str} );
        Vector user_permission_set = (Vector)dbc.executeProcedure();
        return user_permission_set;
    }
}