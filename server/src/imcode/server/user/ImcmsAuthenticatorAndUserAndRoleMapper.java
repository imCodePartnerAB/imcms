package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.commands.*;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.exceptions.IntegrityConstraintViolationException;
import imcode.server.db.exceptions.StringTruncationException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.util.*;

public class ImcmsAuthenticatorAndUserAndRoleMapper implements UserMapper, UserAndRoleRegistry, Authenticator {

    public static final String SPROC_GET_HIGHEST_USER_ID = "GetHighestUserId";

    private static final String SPROC_ADD_USER_ROLE = "AddUserRole";
    private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
    private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

    private static final int USER_EXTERN_ID = 2;

    private Database database;
    private Logger log = Logger.getLogger( ImcmsAuthenticatorAndUserAndRoleMapper.class );
    private static final String SQL_SELECT_USERS = "SELECT user_id, login_name, login_password, first_name, last_name, "
                                                   + "title, company, address, city, zip, country, county_council, "
                                                   + "email, language, active, "
                                                   + "create_date, external "
                                                   + "FROM users";

    public static final String SQL_ROLES_COLUMNS = "roles.role_id, roles.role_name, roles.admin_role, roles.permissions";
    private static final String SQL_SELECT_ALL_ROLES = "SELECT " + SQL_ROLES_COLUMNS + " FROM roles";
    public static final String SQL_SELECT_ROLE_BY_NAME = SQL_SELECT_ALL_ROLES + " WHERE role_name = ?";
    private static final String SQL_SELECT_ROLE_BY_ID = SQL_SELECT_ALL_ROLES + " WHERE role_id = ?";

    public static final String SQL_INSERT_INTO_ROLES = "INSERT INTO roles (role_name, permissions, admin_role) VALUES(?,?,0)";
    private ImcmsServices service;

    public ImcmsAuthenticatorAndUserAndRoleMapper( Database database, ImcmsServices service ) {
        this.database = database;
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
        try {
            String[] user_data = database.executeArrayQuery( "SELECT user_id,\n"
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
                                                    + "external\n"
                                                    + "FROM users\n"
                                                    + "WHERE login_name = ?",
                            new String[] {loginName.trim()} );

            return getUserFromSqlRow( user_data );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    private UserDomainObject getUserFromSqlRow( String[] sqlResult ) {
        UserDomainObject user;

        if ( sqlResult.length == 0 ) {
            user = null;
        } else {
            user = new LazilyLoadedUserDomainObject( 0, false );
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
        user.setLanguageIso639_2( (String)ObjectUtils.defaultIfNull( sqlResult[13], service.getDefaultLanguage() ) );
        user.setActive( 0 != Integer.parseInt( sqlResult[14] ) );
        user.setCreateDate( sqlResult[15] );
        user.setImcmsExternal( 0 != Integer.parseInt( sqlResult[16] ) );
    }

    private RoleDomainObject[] getRolesForUser( UserDomainObject user ) {
        try {
            String sqlStr = SQL_SELECT_ALL_ROLES + ", user_roles_crossref"
                            + " WHERE user_roles_crossref.role_id = roles.role_id"
                            + " AND user_roles_crossref.user_id = ?";
            String[][] sqlResult = database.execute2dArrayQuery( sqlStr, new String[] {"" + user.getId()} );
            RoleDomainObject[] roles = new RoleDomainObject[sqlResult.length];
            for ( int i = 0; i < sqlResult.length; i++ ) {
                String[] sqlRow = sqlResult[i];
                roles[i] = getRoleFromSqlResult( sqlRow );
            }
            return roles;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    /** @return An object representing the user with the given id. */
    public UserDomainObject getUser( int userId ) {
        return new LazilyLoadedUserDomainObject( userId );
    }

    private String[][] sqlSelectAllUsers( boolean includeUserExtern, boolean includeInactiveUsers ) {
        List whereTests = new ArrayList();
        if ( !includeUserExtern ) {
            whereTests.add( "user_id != " + USER_EXTERN_ID );
        }
        if ( !includeInactiveUsers ) {
            whereTests.add( "active = 1" );
        }
        String sqlStr = SQL_SELECT_USERS;
        if ( whereTests.size() > 0 ) {
            sqlStr += " WHERE " + StringUtils.join( whereTests.iterator(), " AND " );
        }
        try {
            return database.execute2dArrayQuery( sqlStr, new String[0] );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    String[] sqlSelectUserById( int userId ) {
        String sqlStr = SQL_SELECT_USERS
                        + " WHERE user_id = ?";

        try {
            return database.executeArrayQuery( sqlStr, new String[] {"" + userId} );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
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
        try {
            database.executeUpdateQuery( "UPDATE users \n"
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
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }

        if ( !user.equals( currentUser ) ) {
            sqlUpdateUserRoles( user );
        }
        removePhoneNumbers( user );
        addPhoneNumbers( user );
    }

    public synchronized void addUser( UserDomainObject user, UserDomainObject currentUser ) throws UserAlreadyExistsException {
        try {
            String newUserId = database.executeStringProcedure( SPROC_GET_HIGHEST_USER_ID, new String[] {} );
            int newIntUserId = Integer.parseInt( newUserId );
            user.setId( newIntUserId );
            String[] usersColumns = new String[] {
                                    "user_id", "login_name", "login_password",
                                    "first_name", "last_name", "title",
                                    "company", "address", "city", "zip",
                                    "country", "county_council", "email",
                                    "external", "active",
                                    "language", "create_date"
                            };

            if ( user.isImcmsExternal() ) {
                user.setPassword( "" );
            }
            database.executeUpdateQuery( "INSERT INTO users (" + StringUtils.join( usersColumns, ',' ) + ")\n"
                                     + "VALUES (" + StringUtils.repeat( "?,", usersColumns.length - 1 )
                                     + "?)", new Object[] {
                                                     ""
                                                     + user.getId(),
                                                             user.getLoginName(),
                                                             user.getPassword(),
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
                                                     user.isImcmsExternal()
                                                     ? "1"
                                                     : "0",
                                                     user.isActive()
                                                     ? "1"
                                                     : "0",
                                                             user.getLanguageIso639_2(),
                                                             new Date()
                                                     } );
            if ( !user.equals( currentUser ) ) {
                sqlAddRolesToUser( user.getRoles(), user );
            }
            addPhoneNumbers( user );
        } catch ( IntegrityConstraintViolationException e ) {
            throw new UserAlreadyExistsException( e );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    private void removePhoneNumbers( UserDomainObject newUser ) {
        String[] sprocParameters = new String[] {String.valueOf( newUser.getId() )};
        try {
            database.executeUpdateProcedure( SPROC_DEL_PHONE_NR, sprocParameters );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    private void addPhoneNumbers( UserDomainObject newUser ) {
        final int PHONE_TYPE_OTHER_PHONE = 0;
        final int PHONE_TYPE_HOME_PHONE = 1;
        final int PHONE_TYPE_WORK_PHONE = 2;
        final int PHONE_TYPE_WORK_MOBILE = 3;
        final int PHONE_TYPE_FAX_PHONE = 4;
        if ( newUser.getHomePhone().length() > 0 ) {
            addPhoneNumber( newUser.getId(), newUser.getHomePhone(), PHONE_TYPE_HOME_PHONE, database );
        }
        if ( newUser.getWorkPhone().length() > 0 ) {
            addPhoneNumber( newUser.getId(), newUser.getWorkPhone(), PHONE_TYPE_WORK_PHONE, database );
        }
        if ( newUser.getMobilePhone().length() > 0 ) {
            addPhoneNumber( newUser.getId(), newUser.getMobilePhone(), PHONE_TYPE_WORK_MOBILE, database );
        }
        if ( newUser.getFaxPhone().length() > 0 ) {
            addPhoneNumber( newUser.getId(), newUser.getFaxPhone(), PHONE_TYPE_FAX_PHONE, database );
        }
        if ( newUser.getOtherPhone().length() > 0 ) {
            addPhoneNumber( newUser.getId(), newUser.getOtherPhone(), PHONE_TYPE_OTHER_PHONE, database );
        }
    }

    /** @deprecated  */
    public String[] getRoleNames( UserDomainObject user ) {
        try {
            return database.executeArrayProcedure( SPROC_GET_USER_ROLES, new String[] {"" + user.getId()} );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[] getAllRoleNames() {
        try {
            String[] roleNamesMinusUsers = database.executeArrayProcedure( SPROC_GET_ALL_ROLES, new String[] {} );

            Set roleNamesSet = new HashSet();
            for ( int i = 0; i < roleNamesMinusUsers.length; i += 2 ) {
                String roleName = roleNamesMinusUsers[i + 1];
                roleNamesSet.add( roleName );
            }

            roleNamesSet.add( RoleConstants.USERS );

            String[] roleNames = (String[])roleNamesSet.toArray( new String[roleNamesSet.size()] );
            Arrays.sort( roleNames );

            return roleNames;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public void addRoleNames( String[] externalRoleNames ) {
        for ( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            this.addRole( externalRoleName );
        }
    }

    public void addRoleToUser( UserDomainObject user,
                               String roleName ) {
        addRole( roleName );
        log.debug( "Trying to assign role " + roleName + " to user " + user.getLoginName() );
        RoleDomainObject role = getRoleByName( roleName );
        sqlAddRoleToUser( role, user );
    }

    private void sqlAddRoleToUser( RoleDomainObject role, UserDomainObject user ) {
        try {
            database.executeUpdateProcedure( SPROC_ADD_USER_ROLE, new String[] {
                                                         String.valueOf( user.getId() ), "" + role.getId()
                                                 } );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public UserDomainObject[] getUsers( boolean includeUserExtern, boolean includeInactiveUsers ) {
        String[][] allUsersSqlResult = sqlSelectAllUsers( includeUserExtern, includeInactiveUsers );
        return getUsersFromSqlRows( allUsersSqlResult );
    }

    public void setUserRoles( UserDomainObject user,
                              String[] roleNames ) {
        this.sqlRemoveAllRoles( user );

        for ( int i = 0; i < roleNames.length; i++ ) {
            String roleName = roleNames[i];
            this.addRoleToUser( user, roleName );
        }
    }

    private void sqlRemoveAllRoles( UserDomainObject user ) {
        try {
            database.executeUpdateQuery( "DELETE FROM user_roles_crossref WHERE user_id = ?", new Object[] { new Integer(user.getId()) } ) ;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public UserDomainObject[] getAllUsersWithRole( RoleDomainObject role ) {
        try {
            if ( null == role ) {
                return new UserDomainObject[] {};
            }
            String[] usersWithRole = database.executeArrayProcedure( SPROC_GET_USERS_WHO_BELONGS_TO_ROLE,
                                                            new String[] {"" + role.getId()} );
            UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

            for ( int i = 0; i < result.length; i++ ) {
                String userIdStr = usersWithRole[i * 2];
                UserDomainObject user = getUser( Integer.parseInt( userIdStr ) );
                result[i] = user;
            }
            return result;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public static void addPhoneNumber( final int newUserId, final String phoneNumber, final int phoneNumberType, Database database ) {
        try {
            database.executeCommand(new InsertIntoTableDatabaseCommand("phones", new String[][] {
                {"user_id", ""+newUserId},
                {"number", phoneNumber},
                {"phonetype_id", ""+phoneNumberType}
            })) ;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public synchronized RoleDomainObject addRole( String roleName ) {
        RoleDomainObject role = getRoleByName( roleName );
        if ( null == role ) {
            role = new RoleDomainObject( roleName );
            try {
                addRole( role );
            } catch ( UserAndRoleRegistryException e ) {
                throw new UnhandledException( e );
            }
        }
        return role;
    }

    void addRole( final RoleDomainObject role ) throws RoleAlreadyExistsException, NameTooLongException {
        try {
            final int unionOfPermissionSetIds = getUnionOfRolePermissionIds( role );
            final int newRoleId;
            newRoleId = ( (Number)database.executeCommand( new TransactionDatabaseCommand() {
                              public Object executeInTransaction( DatabaseConnection connection ) throws DatabaseException {
                                  return connection.executeUpdateAndGetGeneratedKey( SQL_INSERT_INTO_ROLES, new String[] {role.getName(),
                                                                                             ""
                                                                                             + unionOfPermissionSetIds} );
                              }
                          } ) ).intValue();
            role.setId( newRoleId );
        } catch ( IntegrityConstraintViolationException icvse ) {
            throw new RoleAlreadyExistsException( "A role with the name \"" + role.getName()
                                                              + "\" already exists." );
        } catch ( StringTruncationException stse ) {
            throw new NameTooLongException( "Role name too long: "+role.getName() );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    private int getUnionOfRolePermissionIds( RoleDomainObject role ) {
        int unionOfPermissionSetIds = 0;
        RolePermissionDomainObject[] rolePermissions = role.getPermissions();
        for ( int i = 0; i < rolePermissions.length; i++ ) {
            RolePermissionDomainObject rolePermission = rolePermissions[i];
            unionOfPermissionSetIds |= rolePermission.getId();
        }
        return unionOfPermissionSetIds;
    }

    public void deleteRole( RoleDomainObject role ) {
        if ( null == role ) {
            return;
        }
        try {
            DatabaseCommand databaseCommand = new CompositeDatabaseCommand( new DatabaseCommand[] {
                new DeleteWhereColumnEqualsDatabaseCommand( "roles_rights", "role_id", ""+role.getId()),
                new DeleteWhereColumnEqualsDatabaseCommand( "user_roles_crossref", "role_id", ""+role.getId()),
                new DeleteWhereColumnEqualsDatabaseCommand( "roles", "role_id", ""+role.getId()),
            } );
            database.executeCommand( databaseCommand ) ;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public RoleDomainObject[] getAllRoles() {
        try {
            String[][] sqlRows = database.execute2dArrayQuery( SQL_SELECT_ALL_ROLES, new String[0] );
            RoleDomainObject[] roles = new RoleDomainObject[sqlRows.length];
            for ( int i = 0; i < sqlRows.length; i++ ) {
                roles[i] = getRoleFromSqlResult( sqlRows[i] );
            }
            return roles;
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public RoleDomainObject getRoleById( int roleId ) {
        try {
            String[] sqlResult = database.executeArrayQuery( SQL_SELECT_ROLE_BY_ID, new String[] {"" + roleId} );
            return getRoleFromSqlResult( sqlResult );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public RoleDomainObject getRoleByName( String wantedRoleName ) {
        try {
            String[] sqlResult = database.executeArrayQuery( SQL_SELECT_ROLE_BY_NAME, new String[] {wantedRoleName} );
            return getRoleFromSqlResult( sqlResult );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public RoleDomainObject getRoleFromSqlResult( String[] sqlResult ) {
        RoleDomainObject role = null;
        if ( sqlResult.length > 0 ) {
            int roleId = Integer.parseInt( sqlResult[0] );
            String roleName = sqlResult[1];
            int adminRoleId = Integer.parseInt( sqlResult[2] );
            int unionOfRolePermissionIds = Integer.parseInt( sqlResult[3] );
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
        try {
            String sql = SQL_SELECT_USERS + " WHERE user_id != " + USER_EXTERN_ID
                         + " AND ( login_name LIKE ? OR first_name LIKE ? OR last_name LIKE ? )";
            if ( !includeInactiveUsers ) {
                sql += " AND active = 1";
            }
            sql += " ORDER BY last_name, first_name";
            String like = namePrefix + "%";
            String[][] sqlRows = database.execute2dArrayQuery( sql, new String[] {like, like, like} );
            return getUsersFromSqlRows( sqlRows );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    private UserDomainObject[] getUsersFromSqlRows( String[][] sqlRows ) {
        UserDomainObject[] users = new UserDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            users[i] = getUserFromSqlRow( sqlRows[i] );
        }
        return users;
    }

    public void initUserPhoneNumbers( UserDomainObject user ) {
        String[][] phoneNbr = getUserPhoneNumbers( user.getId() );
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

    public void saveRole( RoleDomainObject role ) throws NameTooLongException, RoleAlreadyExistsException {
        if ( 0 == role.getId() ) {
            addRole( role );
        } else {
            saveExistingRole( role );
        }
    }

    private void saveExistingRole( RoleDomainObject role ) {
        int unionOfRolePermissionIds = getUnionOfRolePermissionIds( role );
        try {
            database.executeUpdateQuery( "UPDATE roles SET role_name = ?, permissions = ? WHERE role_id = ?", new String[] {role.getName(),
                                             "" + unionOfRolePermissionIds, "" + role.getId()} );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[][] getUserPhoneNumbers( int userToChangeId ) {
        try {
            return database.execute2dArrayQuery( "SELECT phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename\n"
                                           + "FROM   phones, users, phonetypes, lang_prefixes\n"
                                           + "WHERE  phones.user_id = users.user_id\n"
                                           + "AND    phones.phonetype_id = phonetypes.phonetype_id\n"
                                           + "AND\t   users.language = lang_prefixes.lang_prefix\n"
                                           + "AND    lang_prefixes.lang_id = phonetypes.lang_id\n"
                                           + "AND    phones.user_id = ?", new String[] {"" + userToChangeId} );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public RoleDomainObject[] getUseradminPermissibleRoles( UserDomainObject loggedOnUser ) {
        String[] roleIds;
        try {
            roleIds = database.executeArrayQuery( "SELECT role_id FROM roles\n"
                                         + "WHERE roles.role_id IN\n"
                                         + "( SELECT role_id\n"
                                         + "FROM useradmin_role_crossref\n"
                                         + "WHERE user_id = ? )", new String[] {"" + loggedOnUser.getId()} );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }

        List useradminPermissibleRolesList = new ArrayList();
        for ( int i = 0; i < roleIds.length; i++ ) {
            useradminPermissibleRolesList.add( getRoleById( Integer.parseInt( roleIds[i] ) ) );
        }
        return (RoleDomainObject[])useradminPermissibleRolesList.toArray( new RoleDomainObject[useradminPermissibleRolesList.size()] );
    }

    public RoleDomainObject[] getAllRolesWithPermission( RolePermissionDomainObject rolePermission ) {
        RoleDomainObject[] allRoles = getAllRoles();
        List rolesWithPermissionList = new ArrayList( allRoles.length );
        for ( int i = 0; i < allRoles.length; i++ ) {
            RoleDomainObject role = allRoles[i];
            if ( role.hasPermission( rolePermission ) ) {
                rolesWithPermissionList.add( role );
            }
        }
        return (RoleDomainObject[])rolesWithPermissionList.toArray( new RoleDomainObject[rolesWithPermissionList.size()] );
    }

    public UserDomainObject getDefaultUser() {
        return getUser( UserDomainObject.DEFAULT_USER_ID );
    }
}