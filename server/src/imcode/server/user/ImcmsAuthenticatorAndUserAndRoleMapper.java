package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;
import imcode.server.ImcmsServices;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class ImcmsAuthenticatorAndUserAndRoleMapper implements UserMapper, UserAndRoleRegistry, Authenticator {

    public static final String SPROC_GET_HIGHEST_USER_ID = "GetHighestUserId";

    private static final String SPROC_ADD_USER_ROLE = "AddUserRole";
    private static final String SPROC_ROLE_DELETE = "RoleDelete";
    private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
    private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
    private static final String SPROC_DEL_USER_ROLES = "DelUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

    private static final String SPROC_PHONE_NBR_ADD = "phoneNbrAdd";
    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

    private static final int USER_EXTERN_ID = 2;

    private ImcmsServices service;
    private Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserAndRoleMapper.class );
    private static final String SQL_SELECT_USERS = "SELECT user_id, login_name, login_password, first_name, last_name, "
                                                   + "title, company, address, city, zip, country, county_council, "
                                                   + "email, language, active, "
                                                   + "create_date, external "
                                                   + "FROM users";

    public static final String SQL_ROLES_COLUMNS = "roles.role_id, roles.role_name, roles.admin_role, roles.permissions" ;
    private static final String SQL_SELECT_ALL_ROLES = "SELECT "+SQL_ROLES_COLUMNS+" FROM roles";
    public static final String SQL_SELECT_ROLE_BY_NAME = SQL_SELECT_ALL_ROLES + " WHERE role_name = ?";
    private static final String SQL_SELECT_ROLE_BY_ID = SQL_SELECT_ALL_ROLES + " WHERE role_id = ?";

    public static final String SQL_INSERT_INTO_ROLES = "INSERT INTO roles (role_name, permissions, admin_role) VALUES(?,?,0) "
                       + "SELECT @@IDENTITY";

    public ImcmsAuthenticatorAndUserAndRoleMapper( ImcmsServices service ) {
        this.service = service;
    }

    public boolean authenticate( String loginName, String password ) {
        boolean userExistsAndPasswordIsCorrect = false;
        UserDomainObject user = getUser( loginName );
        if ( null != user ) {
            String login_password_from_db = user.getPassword();
            String login_password_from_form = password;

            if ( login_password_from_db.equals( login_password_from_form ) && user.isActive() ) {
                userExistsAndPasswordIsCorrect = true;
            } else if ( !user.isActive() ) {
                userExistsAndPasswordIsCorrect = false;
            } else {
                userExistsAndPasswordIsCorrect = false;
            }
        }

        return userExistsAndPasswordIsCorrect;
    }

    public UserDomainObject getUser( String loginName ) {
        String[] user_data = service.sqlQuery( "SELECT user_id,\n"
                                                   + "login_name,\n"
                                                   + "login_password,\n"
                                                   + "first_name,\n"
                                                   + "last_name,\n"
                                                   + "title,\n"
                                                   + "company,\n"
                                                   + "address,\n"
                                                   + "city,\n"
                                                   + "zip,\n"
                                                   + "country,\n"
                                                   + "county_council,\n"
                                                   + "email,\n"
                                                   + "language,\n"
                                                   + "active,\n"
                                                   + "create_date,\n"
                                                   + "[external]\n"
                                                   + "FROM users\n"
                                                   +"WHERE login_name = ?",
                                               new String[]{loginName.trim()} );

        return getUserFromSqlRow( user_data );
    }

    private UserDomainObject getUserFromSqlRow( String[] sqlResult ) {
        UserDomainObject user;

        if ( sqlResult.length == 0 ) {
            user = null;
        } else {
            user = new LazilyLoadedUserDomainObject(0, false);
            initUserFromSqlData( user, sqlResult );
        }
        return user;
    }

    void initUserFromSqlData( UserDomainObject user, String[] sqlResult ) {
        user.setId( Integer.parseInt( sqlResult[0] ) );
        user.setLoginName( sqlResult[1] );
        user.setPassword( sqlResult[2].trim() );
        user.setFirstName( sqlResult[3] );
        user.setLastName( sqlResult[4] );
        user.setTitle( sqlResult[5] );
        user.setCompany( sqlResult[6] );
        user.setAddress( sqlResult[7] );
        user.setCity( sqlResult[8] );
        user.setZip( sqlResult[9] );
        user.setCountry( sqlResult[10] );
        user.setCountyCouncil( sqlResult[11] );
        user.setEmailAddress( sqlResult[12] );
        user.setLanguageIso639_2( (String)ObjectUtils.defaultIfNull( sqlResult[13], service.getDefaultLanguageAsIso639_2() ) );
        user.setActive( 0 != Integer.parseInt( sqlResult[14] ) );
        user.setCreateDate( sqlResult[15] );
        user.setImcmsExternal( 0 != Integer.parseInt( sqlResult[16] ) );
    }

    private RoleDomainObject[] getRolesForUser( UserDomainObject user ) {
        String sqlStr = SQL_SELECT_ALL_ROLES+", user_roles_crossref"
                        + " WHERE user_roles_crossref.role_id = roles.role_id"
                        + " AND user_roles_crossref.user_id = ?";
        String[][] sqlResult = service.sqlQueryMulti( sqlStr, new String[]{"" + user.getId()} );
        RoleDomainObject[] roles = new RoleDomainObject[sqlResult.length];
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];
            roles[i] = getRoleFromSqlResult( sqlRow );
        }
        return roles;
    }

    /**
     * @return An object representing the user with the given id.
     */
    public UserDomainObject getUser( int userId ) {
        return new LazilyLoadedUserDomainObject( userId );
    }

    private String[][] sqlSelectAllUsers( boolean includeUserExtern, boolean includeInactiveUsers ) {
        List whereTests = new ArrayList() ;
        if ( !includeUserExtern ) {
            whereTests.add("user_id != " + USER_EXTERN_ID);
        }
        if ( !includeInactiveUsers ) {
            whereTests.add("active = 1");
        }
        String sqlStr = SQL_SELECT_USERS;
        if (whereTests.size() > 0) {
            sqlStr += " WHERE "+StringUtils.join( whereTests.iterator(), " AND ") ;
        }
        return service.sqlQueryMulti( sqlStr, new String[0] );
    }

    String[] sqlSelectUserById( int userId ) {
        String sqlStr = SQL_SELECT_USERS
                        + " WHERE user_id = ?";

        return service.sqlQuery( sqlStr, new String[]{"" + userId} );
    }

    public void saveUser( String loginName, UserDomainObject tempUser, UserDomainObject currentUser ) {
        UserDomainObject imcmsUser = getUser( loginName );
        tempUser.setId( imcmsUser.getId() );
        tempUser.setLoginName( loginName );
        saveUser( tempUser, currentUser );
    }

    public void saveUser( UserDomainObject user, UserDomainObject currentUser ) {
        String[] params = {
            user.getLoginName(),
            null == user.getPassword() ? "" : user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getTitle(),
            user.getCompany(),
            user.getAddress(),
            user.getCity(),
            user.getZip(),
            user.getCountry(),
            user.getCountyCouncil(),
            user.getEmailAddress(),
            user.isImcmsExternal() ? "1" : "0",
            user.isActive() ? "1" : "0",
            user.getLanguageIso639_2(),
            "" + user.getId(),
        };
        service.sqlUpdateQuery( "UPDATE users \n"
                                + "SET login_name = ?,\n"
                                + "login_password = ?,\n"
                                + "first_name = ?,\n"
                                + "last_name = ?,\n"
                                + "title = ?,\n"
                                + "company = ?,\n"
                                + "address =  ?,\n"
                                + "city = ?,\n"
                                + "zip = ?,\n"
                                + "country = ?,\n"
                                + "county_council = ?,\n"
                                + "email = ?,\n"
                                + "external = ?,\n"
                                + "active = ?,\n"
                                + "language = ?\n"
                                + "WHERE user_id = ?", params );

        if (!user.equals( currentUser )) {
            sqlUpdateUserRoles( user );
        }
        removePhoneNumbers( user );
        addPhoneNumbers( user );
    }

    public synchronized void addUser( UserDomainObject user, UserDomainObject currentUser ) {
        String newUserId = service.sqlProcedureStr( SPROC_GET_HIGHEST_USER_ID, new String[]{} );
        int newIntUserId = Integer.parseInt( newUserId );
        user.setId( newIntUserId );
        String[] usersColumns = new String[] {
            "user_id", "login_name", "login_password",
            "first_name", "last_name", "title",
            "company", "address", "city", "zip",
            "country", "county_council", "email",
            "external", "active",
            "language"
        } ;

        service.sqlUpdateQuery( "INSERT INTO users ("+StringUtils.join( usersColumns, ',')+", create_date)\n"
                                + "VALUES ("+StringUtils.repeat( "?,", usersColumns.length )+" getDate())", new String[] {
                                    ""+user.getId(),user.getLoginName(),user.getPassword(),
                                    user.getFirstName(), user.getLastName(), user.getTitle(),
                                    user.getCompany(), user.getAddress(), user.getCity(), user.getZip(),
                                    user.getCountry(), user.getCountyCouncil(), user.getEmailAddress(),
                                    user.isImcmsExternal() ? "1" : "0", user.isActive() ? "1" : "0",
                                    user.getLanguageIso639_2()
                                }) ;
        if (!user.equals( currentUser )) {
            sqlAddRolesToUser( user.getRoles(), user );
        }
        addPhoneNumbers( user );
    }

    private void removePhoneNumbers( UserDomainObject newUser ) {
        staticSprocDelPhoneNr( service, newUser.getId() );
    }

    private void addPhoneNumbers( UserDomainObject newUser ) {
        final int PHONE_TYPE_OTHER_PHONE = 0;
        final int PHONE_TYPE_HOME_PHONE = 1;
        final int PHONE_TYPE_WORK_PHONE = 2;
        final int PHONE_TYPE_WORK_MOBILE = 3;
        final int PHONE_TYPE_FAX_PHONE = 4;
        if ( newUser.getHomePhone().length() > 0 ){
            staticSprocPhoneNbrAdd( service, newUser.getId(), newUser.getHomePhone(), PHONE_TYPE_HOME_PHONE ) ;
        }
        if ( newUser.getWorkPhone().length() > 0 ){
            staticSprocPhoneNbrAdd( service, newUser.getId(), newUser.getWorkPhone(), PHONE_TYPE_WORK_PHONE );
        }
        if ( newUser.getMobilePhone().length() > 0 ){
            staticSprocPhoneNbrAdd( service, newUser.getId(), newUser.getMobilePhone(), PHONE_TYPE_WORK_MOBILE );
        }
        if ( newUser.getFaxPhone().length() > 0 ){
            staticSprocPhoneNbrAdd( service, newUser.getId(), newUser.getFaxPhone(), PHONE_TYPE_FAX_PHONE );
        }
        if ( newUser.getOtherPhone().length() > 0 ){
            staticSprocPhoneNbrAdd( service, newUser.getId(), newUser.getOtherPhone(), PHONE_TYPE_OTHER_PHONE );
        }
    }

    /** @deprecated */
    public String[] getRoleNames( UserDomainObject user ) {
        String[] roleNames = service.sqlProcedure( SPROC_GET_USER_ROLES, new String[]{"" + user.getId()} );
        return roleNames;
    }

    public String[] getAllRoleNames() {
        String[] roleNamesMinusUsers = service.sqlProcedure( SPROC_GET_ALL_ROLES, new String[]{} );

        Set roleNamesSet = new HashSet();
        for ( int i = 0; i < roleNamesMinusUsers.length; i += 2 ) {
            String roleName = roleNamesMinusUsers[i + 1];
            roleNamesSet.add( roleName );
        }

        roleNamesSet.add( RoleConstants.USERS );

        String[] roleNames = (String[])roleNamesSet.toArray( new String[roleNamesSet.size()] );
        Arrays.sort( roleNames );

        return roleNames;
    }

    public void addRoleNames( String[] externalRoleNames ) {
        for ( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            this.addRole( externalRoleName );
        }
    }

    public void addRoleToUser( UserDomainObject user, String roleName ) {
        addRole( roleName );
        log.debug( "Trying to assign role " + roleName + " to user " + user.getLoginName() );
        RoleDomainObject role = getRoleByName( roleName );
        sqlAddRoleToUser( role, user );
    }

    private void sqlAddRoleToUser( RoleDomainObject role, UserDomainObject user ) {
        service.sqlUpdateProcedure( SPROC_ADD_USER_ROLE, new String[]{
            String.valueOf( user.getId() ), "" + role.getId()
        } );
    }

    public UserDomainObject[] getUsers( boolean includeUserExtern, boolean includeInactiveUsers ) {
        String[][] allUsersSqlResult = sqlSelectAllUsers( includeUserExtern, includeInactiveUsers );
        return getUsersFromSqlRows( allUsersSqlResult );
    }

    public void setUserRoles( UserDomainObject user, String[] roleNames ) {
        this.sqlRemoveAllRoles( user );

        for ( int i = 0; i < roleNames.length; i++ ) {
            String roleName = roleNames[i];
            this.addRoleToUser( user, roleName );
        }
    }

    private void sqlRemoveAllRoles( UserDomainObject user ) {
        service.sqlUpdateProcedure( SPROC_DEL_USER_ROLES, new String[]{"" + user.getId(), "-1"} );
    }

    public UserDomainObject[] getAllUsersWithRole( RoleDomainObject role ) {
        if ( null == role ) {
            return new UserDomainObject[]{};
        }
        String[] usersWithRole = service.sqlProcedure( SPROC_GET_USERS_WHO_BELONGS_TO_ROLE,
                                                       new String[]{"" + role.getId()} );
        UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

        for ( int i = 0; i < result.length; i++ ) {
            String userIdStr = usersWithRole[i * 2];
            UserDomainObject user = getUser( Integer.parseInt( userIdStr ) );
            result[i] = user;
        }
        return result;
    }

    public static void staticSprocPhoneNbrAdd( ImcmsServices service,
                                               int newUserId, String phoneNumber, int phoneNumberType ) {
        String[] sprocParameters = new String[]{
            String.valueOf( newUserId ), phoneNumber, String.valueOf( phoneNumberType )
        };
        service.sqlUpdateProcedure( SPROC_PHONE_NBR_ADD, sprocParameters );
    }

    private static void staticSprocDelPhoneNr( ImcmsServices service, int userId ) {
        String[] sprocParameters = new String[]{String.valueOf( userId )};
        service.sqlUpdateProcedure( SPROC_DEL_PHONE_NR, sprocParameters );
    }

    public synchronized RoleDomainObject addRole( String roleName ) {
        RoleDomainObject role = new RoleDomainObject( roleName );
        saveNewRole( role );
        return role ;
    }

    private void saveNewRole( RoleDomainObject role ) {
        int unionOfPermissionSetIds = getUnionOfRolePermissionIds( role );
        int newRoleId = Integer.parseInt( service.sqlQueryStr( SQL_INSERT_INTO_ROLES, new String[] { role.getName(), ""+unionOfPermissionSetIds } ) ) ;
        role.setId( newRoleId );
    }

    private int getUnionOfRolePermissionIds( RoleDomainObject role ) {
        int unionOfPermissionSetIds = 0 ;
        RolePermissionDomainObject[] rolePermissions = role.getPermissions() ;
        for ( int i = 0; i < rolePermissions.length; i++ ) {
            RolePermissionDomainObject rolePermission = rolePermissions[i];
            unionOfPermissionSetIds |= rolePermission.getId() ;
        }
        return unionOfPermissionSetIds;
    }

    public void deleteRole( RoleDomainObject role ) {
        if ( null == role ) {
            return;
        }
        service.sqlUpdateProcedure( SPROC_ROLE_DELETE, new String[]{"" + role.getId()} );
    }

    public RoleDomainObject[] getAllRoles() {
        String[][] sqlRows = service.sqlQueryMulti( SQL_SELECT_ALL_ROLES, new String[0] ) ;
        RoleDomainObject[] roles = new RoleDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            roles[i] = getRoleFromSqlResult( sqlRows[i] );
        }
        return roles ;
    }

    public RoleDomainObject getRoleById( int roleId ) {
        String[] sqlResult = service.sqlQuery( SQL_SELECT_ROLE_BY_ID, new String[]{"" + roleId} );
        return getRoleFromSqlResult( sqlResult );
    }

    public RoleDomainObject getRoleByName( String wantedRoleName ) {
        String[] sqlResult = service.sqlQuery( SQL_SELECT_ROLE_BY_NAME, new String[]{wantedRoleName} );
        return getRoleFromSqlResult( sqlResult );
    }

    public RoleDomainObject getRoleFromSqlResult( String[] sqlResult ) {
        RoleDomainObject role = null;
        if ( sqlResult.length > 0 ) {
            int roleId = Integer.parseInt( sqlResult[0] );
            String roleName = sqlResult[1];
            int adminRoleId = Integer.parseInt( sqlResult[2] );
            int unionOfRolePermissionIds = Integer.parseInt(sqlResult[3]) ;
            role = new RoleDomainObject( roleId, roleName, adminRoleId );
            role.addUnionOfPermissionIdsToRole( unionOfRolePermissionIds );
        }
        return role;
    }

    public void sqlUpdateUserRoles( UserDomainObject tempUser ) {
        tempUser.addRole( RoleDomainObject.USERS );
        RoleDomainObject[] userRoles = tempUser.getRoles();
        sqlRemoveAllRoles( tempUser );
        sqlAddRolesToUser( userRoles, tempUser );
    }

    private void sqlAddRolesToUser( RoleDomainObject[] userRoles, UserDomainObject user ) {
        for ( int i = 0; i < userRoles.length; i++ ) {
            RoleDomainObject userRole = userRoles[i];
            sqlAddRoleToUser( userRole, user );
        }
    }

    public UserDomainObject[] getAllUsers() {
        return getUsers( true, true );
    }

    public UserDomainObject[] findUsersByNamePrefix( String namePrefix, boolean includeInactiveUsers ) {
        String sql = SQL_SELECT_USERS + " WHERE user_id != " + USER_EXTERN_ID
                     + " AND ( login_name LIKE ? + '%' OR first_name LIKE ? + '%' OR last_name LIKE ? + '%' )";
        if ( !includeInactiveUsers ) {
            sql += " AND active = 1";
        }
        String[][] sqlRows = service.sqlQueryMulti( sql, new String[]{namePrefix, namePrefix, namePrefix} );
        return getUsersFromSqlRows( sqlRows );
    }

    private UserDomainObject[] getUsersFromSqlRows( String[][] sqlRows ) {
        UserDomainObject[] users = new UserDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            users[i] = getUserFromSqlRow( sqlRows[i] );
        }
        return users;
    }

    public void initUserPhoneNumbers( UserDomainObject user ) {
        String[][] phoneNbr = getUserPhoneNumbers( user.getId() ) ;
        String workPhone = "";
        String mobilePhone = "";
        String homePhone = "";

        if ( phoneNbr != null ) {
            for ( int i = 0; i < phoneNbr.length; i++ ) {
                if ( "2".equals( phoneNbr[i][3] ) ) {
                    workPhone = phoneNbr[i][1];
                } else if ( "3".equals( phoneNbr[i][3] ) ) {
                    mobilePhone = phoneNbr[i][1];
                } else if ( "1".equals( phoneNbr[i][3] ) ) {
                    homePhone = phoneNbr[i][1];
                }
            }
        }
        user.setWorkPhone( workPhone );
        user.setMobilePhone( mobilePhone );
        user.setHomePhone( homePhone );
    }

    public void initUserAttributes( UserDomainObject user ) {
        initUserFromSqlData( user, sqlSelectUserById( user.getId() ) );
    }

    public void initUserRoles( UserDomainObject user ) {
        user.setRoles( getRolesForUser( user ) );
    }

    public void saveRole( RoleDomainObject role ) {
        if (0 == role.getId()) {
            saveNewRole(role) ;
        } else {
            saveExistingRole( role );
        }
    }

    private void saveExistingRole( RoleDomainObject role ) {
        int unionOfRolePermissionIds = getUnionOfRolePermissionIds( role );
        service.sqlUpdateQuery( "UPDATE roles SET role_name = ?, permissions = ? WHERE role_id = ?", new String[] { role.getName(), ""+unionOfRolePermissionIds, ""+role.getId() } ) ;
    }

    public String[][] getUserPhoneNumbers( int userToChangeId ) {
        return service.sqlQueryMulti( "SELECT phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename\n"
                                     + "FROM   phones, users, phonetypes, lang_prefixes\n"
                                     + "WHERE  phones.user_id = users.user_id\n"
                                     + "AND    phones.phonetype_id = phonetypes.phonetype_id\n"
                                     + "AND\t   users.language = lang_prefixes.lang_prefix\n"
                                     + "AND    lang_prefixes.lang_id = phonetypes.lang_id\n"
                                     + "AND    phones.user_id = ?", new String[]{"" + userToChangeId} );
    }

    public RoleDomainObject[] getUseradminPermissibleRoles( UserDomainObject loggedOnUser ){
        String[] roleIds =  service.sqlQuery( "SELECT role_id FROM roles\n"
                                            + "WHERE roles.role_id IN\n"
                                                    + "( SELECT role_id\n"
                                                       + "FROM useradmin_role_crossref\n"
                                                       + "WHERE user_id = ? )", new String[]{""+loggedOnUser.getId()} );

        List  useradminPermissibleRolesList = new ArrayList();
        for (int i = 0; i < roleIds.length; i++ ){
            useradminPermissibleRolesList.add( getRoleById( Integer.parseInt(roleIds[i]) ) );
        }
        return (RoleDomainObject[])useradminPermissibleRolesList.toArray( new RoleDomainObject[useradminPermissibleRolesList.size()]);
    }



    public RoleDomainObject[] getAllRolesWithPermission( RolePermissionDomainObject rolePermission ) {
        RoleDomainObject[] allRoles = getAllRoles() ;
        List rolesWithPermissionList = new ArrayList( allRoles.length );
        for ( int i = 0; i < allRoles.length; i++ ) {
            RoleDomainObject role = allRoles[i];
            if (role.hasPermission( rolePermission )) {
                rolesWithPermissionList.add( role );
            }
        }
        return (RoleDomainObject[])rolesWithPermissionList.toArray( new RoleDomainObject[rolesWithPermissionList.size()] );
    }
}