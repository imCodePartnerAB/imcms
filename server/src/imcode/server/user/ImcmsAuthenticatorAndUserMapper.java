package imcode.server.user;

import imcode.server.IMCServiceInterface;
import imcode.server.db.DatabaseService;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.RoleConstants;

import java.util.*;

public class ImcmsAuthenticatorAndUserMapper implements UserAndRoleMapper, Authenticator {

    private static final String SPROC_GET_HIGHEST_USER_ID = "GetHighestUserId";
    private static final String SPROC_ADD_NEW_USER = "AddNewUser";
    private static final String SPROC_UPDATE_USER = "UpdateUser";

    private static final String SPROC_GET_ROLE_ID_BY_ROLE_NAME = "GetRoleIdByRoleName";
    private static final String SPROC_ADD_USER_ROLE = "AddUserRole";
    private static final String SPROC_DEL_USER_ROLES = "DelUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

    private static final String SPROC_PHONE_NBR_ADD = "phoneNbrAdd";
    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

    private IMCServiceInterface service;
    private DatabaseService databaseService;
    private Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserMapper.class );

    public ImcmsAuthenticatorAndUserMapper( IMCServiceInterface service ) {
        this.service = service;
        databaseService = service.getDatabaseService();
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

    /**
     @return An object representing the user with the given id.
     **/
    public UserDomainObject getUser( int userId ) {
        DatabaseService.Table_users userData = databaseService.sproc_GetUserInfo( userId );
        UserDomainObject result = initUser( userData );
        return result;
    }

    public UserDomainObject getUser( String loginName ) {
        loginName = loginName.trim();
        UserDomainObject result = null;
        DatabaseService.Table_users user_data = databaseService.sproc_GetUserByLogin( loginName );
        if( null != user_data ) {
            result = initUser( user_data );
        } else {
            result = null;
        }
        return result;
    }

    private UserDomainObject initUser( DatabaseService.Table_users user_data ) {
        UserDomainObject result;
        result = new UserDomainObject();

        result.setUserId( user_data.user_id );
        result.setLoginName( user_data.login_name );
        result.setPassword( user_data.login_password );
        result.setFirstName( user_data.first_name );
        result.setLastName( user_data.last_name );
        result.setTitle( user_data.title );
        result.setCompany( user_data.company );
        result.setAddress( user_data.address );
        result.setCity( user_data.city );
        result.setZip( user_data.zip );
        result.setCountry( user_data.country );
        result.setCountyCouncil( user_data.county_council );
        result.setEmailAddress( user_data.email );
        result.setLangId( user_data.lang_id );
        result.setUserType( user_data.user_type );
        result.setActive( user_data.active );
        result.setCreateDate( new Date( user_data.create_date.getTime() ) );
        result.setImcmsExternal( user_data.external );

        DatabaseService.Table_lang_prefixes langPrefix = databaseService.sproc_GetLangPrefixFromId( user_data.lang_id );
        if( null == langPrefix ) {
            result.setLangPrefix( service.getDefaultLanguage() );
        } else {
            result.setLangPrefix( langPrefix.lang_prefix );
        }

        DatabaseService.JoinedTables_phones_phonetypes[] phoneNumbers = databaseService.sproc_GetUserPhoneNumbers( user_data.user_id );
        if( phoneNumbers != null ) {
            for( int i = 0; i < phoneNumbers.length; i++ ) {
                if( 2 == phoneNumbers[i].phonetype_id ) {
                    result.setWorkPhone( phoneNumbers[i].number );
                } else if( 3 == phoneNumbers[i].phonetype_id ) {
                    result.setMobilePhone( phoneNumbers[i].number );
                } else if( 1 == phoneNumbers[i].phonetype_id ) {
                    result.setHomePhone( phoneNumbers[i].number );
                }
            }
        }
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
        String[] roleNames = databaseService.sproc_GetUserRoles(user.getUserId());
        return roleNames;
    }

    public String[] getAllRoleNames() {
        DatabaseService.Table_roles[] roles = databaseService.sproc_GetAllRoles_but_user();
        String[] result = new String[ roles.length + 1 ];
        for (int i = 0; i < roles.length; i++) {
            DatabaseService.Table_roles role = roles[i];
            result[i] = role.role_name;
        }
        result[roles.length] = RoleConstants.USERS;
        return result;
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

    public UserDomainObject[] getAllUsers() {
        DatabaseService.PartOfTable_users[] users = databaseService.sproc_GetAllUsersInList();
        UserDomainObject[] result = new UserDomainObject[users.length];
        for (int i = 0; i < users.length; i++) {
            DatabaseService.PartOfTable_users user = users[i];
            result[i] = getUser( user.user_id );
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
        service.sqlUpdateProcedure( SPROC_DEL_USER_ROLES, new String[]{"" + user.getUserId(), "-1"} );
    }

    public UserDomainObject[] getAllUsersWithRole( String roleName ) {
        String rolesIdStr = staticSprocCallGetRoleIdByRoleName( service, roleName );
        String[] usersWithRole = service.sqlProcedure( SPROC_GET_USERS_WHO_BELONGS_TO_ROLE, new String[]{rolesIdStr} );
        UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

        for( int i = 0; i < result.length; i++ ) {
            String userIdStr = usersWithRole[i * 2];
            UserDomainObject user = getUser( Integer.parseInt( userIdStr ) );
            result[i] = user;
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
        String[] sprocParameters = new String[]{String.valueOf( newUserId ), phoneNumber, String.valueOf( phoneNumberType )};
        service.sqlUpdateProcedure( SPROC_PHONE_NBR_ADD, sprocParameters );
    }

    private static void staticSprocDelPhoneNr( IMCServiceInterface service, int userId ) {
        String[] sprocParameters = new String[]{String.valueOf( userId )};
        service.sqlUpdateProcedure( SPROC_DEL_PHONE_NR, sprocParameters );
    }

    public boolean hasSuperAdminRole( UserDomainObject user ) {
        String[] userRoleNames = this.getRoleNames( user );
        boolean userHasSuperAdminRole = Arrays.asList( userRoleNames ).contains( RoleConstants.SUPER_ADMIN );
        return userHasSuperAdminRole;
    }

    public synchronized void addRole( String roleName ) {
        int roleId = callSprocRoleFindName( roleName );
        boolean roleNotExists = (-1 == roleId);
        if( roleNotExists ) {
            databaseService.sproc_RoleAddNew( roleName );
        }
    }

    public void deleteRole( String roleName ) {
        int roleId = callSprocRoleFindName( roleName );
        boolean roleNotExists = (-1 == roleId);
        if( roleNotExists ) {
            databaseService.sproc_RoleDelete( roleId );
        }
    }

    /**
     *
     * @param roleName
     * @return roleId
     */
    private int callSprocRoleFindName( String roleName ) {
        int roleId = databaseService.sproc_RoleFindName( roleName );
        return roleId;
    }

    private void callSprocModifyUserProcedure( String modifyUserProcedureName, UserDomainObject tempUser ) {
        String[] params = {String.valueOf( tempUser.getUserId() ),
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
                           tempUser.isActive() ? "1" : "0"};
        service.sqlUpdateProcedure( modifyUserProcedureName, params );
    }

}