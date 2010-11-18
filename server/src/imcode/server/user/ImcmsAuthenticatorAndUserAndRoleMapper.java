package imcode.server.user;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.*;
import com.imcode.db.exceptions.IntegrityConstraintViolationException;
import com.imcode.db.exceptions.StringTruncationException;
import com.imcode.imcms.db.DatabaseUtils;
import com.imcode.imcms.db.StringArrayResultSetHandler;
import imcode.server.ImcmsServices;
import imcode.util.DateConstants;
import imcode.util.Utility;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImcmsAuthenticatorAndUserAndRoleMapper implements UserAndRoleRegistry, Authenticator, RoleGetter {

    private final static Logger log = Logger.getLogger(ImcmsAuthenticatorAndUserAndRoleMapper.class);

    private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
    private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

    private static final int USER_EXTERN_ID = 2;

    private static final String SQL_SELECT_USERS = "SELECT user_id, login_name, login_password, first_name, last_name, "
                                                   + "title, company, address, city, zip, country, county_council, "
                                                   + "email, language, active, "
                                                   + "create_date, " + (DatabaseUtils.isDatabaseMSSql() ? "[external]" : "external") 
                                                   + ", session_id, remember_cd FROM users";


    public static final String SQL_ROLES_COLUMNS = "roles.role_id, roles.role_name, roles.admin_role, roles.permissions";
    private static final String SQL_SELECT_ALL_ROLES = "SELECT " + SQL_ROLES_COLUMNS + " FROM roles";
    private static final String SQL_SELECT_ALL_ROLES_EXCEPT_USERS_ROLE = SQL_SELECT_ALL_ROLES
                                                                         + " WHERE roles.role_id != " + RoleId.USERS_ID + " ORDER BY roles.role_name";

    public static final String SQL_SELECT_ROLE_BY_NAME = SQL_SELECT_ALL_ROLES + " WHERE role_name = ?";
    private static final String SQL_SELECT_ROLE_BY_ID = SQL_SELECT_ALL_ROLES + " WHERE role_id = ?";

    public static final String SQL_INSERT_INTO_ROLES = "INSERT INTO roles (role_name, permissions, admin_role) VALUES(?,?,0)";
    private static final String TABLE__USERADMIN_ROLE_CROSSREF = "useradmin_role_crossref";
    private static final String SQL__SELECT_USER_BY_ID = SQL_SELECT_USERS
                                                         + " WHERE user_id = ?";

    private static final String SQL_UPDATE_USER_SESSION = "update users set session_id = ? where user_id = ?";

    private static final String SQL_SELECT_USER_SESSION = "select session_id from users where user_id = ?";
    
    private static final String SQL_UPDATE_USER_REMEMBER_CD = "UPDATE users SET remember_cd = ? WHERE user_id = ?";

    private final ImcmsServices services;

    public ImcmsAuthenticatorAndUserAndRoleMapper(ImcmsServices services) {
        this.services = services;
    }

    public boolean authenticate(String loginName, String password) {
        boolean userExistsAndPasswordIsCorrect = false;
        UserDomainObject user = getUser(loginName);
        if ( null != user ) {
            String password_from_db = user.getPassword();

            userExistsAndPasswordIsCorrect = password_from_db.equals(password) && user.isActive();
        }

        return userExistsAndPasswordIsCorrect;
    }

    public UserDomainObject getUser(String loginName) {
        return getUserFromSqlRow(sqlSelectUserByName(loginName));
    }

    private String[] sqlSelectUserByName(String loginName) {
        final Object[] parameters = new String[] { loginName.trim() };
        return (String[]) services.getDatabase().execute(new SqlQueryCommand(SQL_SELECT_USERS
                                                                             + " WHERE login_name = ?", parameters, Utility.STRING_ARRAY_HANDLER));
    }

    private UserDomainObject getUserFromSqlRow(String[] sqlResult) {
        UserDomainObject user;

        if ( sqlResult.length == 0 ) {
            user = null;
        } else {
            user = new UserDomainObject(Integer.parseInt(sqlResult[0]));
            initUserFromSqlData(user, sqlResult);
            initUserRoles(user);
            initUserPhoneNumbers(user);
            initUserUserAdminRoles(user);
        }
        return user;
    }

    void initUserFromSqlData(UserDomainObject user, String[] sqlResult) {
        user.setLoginName(sqlResult[1]);
        user.setPassword(sqlResult[2].trim());
        user.setFirstName(sqlResult[3]);
        user.setLastName(sqlResult[4]);
        user.setTitle(sqlResult[5]);
        user.setCompany(sqlResult[6]);
        user.setAddress(sqlResult[7]);
        user.setCity(sqlResult[8]);
        user.setZip(sqlResult[9]);
        user.setCountry(sqlResult[10]);
        user.setProvince(sqlResult[11]);
        user.setEmailAddress(sqlResult[12]);
        user.setLanguageIso639_2((String) ObjectUtils.defaultIfNull(sqlResult[13], services.getLanguageMapper().getDefaultLanguage()));
        user.setActive(0 != Integer.parseInt(sqlResult[14]));
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        user.setCreateDate(Utility.parseDateFormat(dateFormat, sqlResult[15]));
        user.setImcmsExternal(0 != Integer.parseInt(sqlResult[16]));
        user.setSessionId(sqlResult[17]);
        user.setRememberCd(sqlResult[18]);
    }

    private RoleId[] getRoleReferencesForUser(UserDomainObject user) {
        try {
            String sqlStr = SQL_SELECT_ALL_ROLES + ", user_roles_crossref"
                            + " WHERE user_roles_crossref.role_id = roles.role_id"
                            + " AND user_roles_crossref.user_id = ?";
            final Object[] parameters = new String[] { "" + user.getId() };
            String[][] sqlResult = (String[][]) services.getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
            RoleId[] roleReferences = new RoleId[sqlResult.length];
            for ( int i = 0; i < sqlResult.length; i++ ) {
                String[] sqlRow = sqlResult[i];
                roleReferences[i] = getRoleReferenceFromSqlResult(sqlRow);
            }
            return roleReferences;
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }
    
    public void updateUserRememberCd(final UserDomainObject user) {
    	services.getDatabase().execute(new SqlUpdateCommand(SQL_UPDATE_USER_REMEMBER_CD, 
    			new Object[] { user.getRememberCd(), user.getId() }));
    }

    private RoleId getRoleReferenceFromSqlResult(String[] sqlRow) {
        return new RoleId(Integer.parseInt(sqlRow[0]));
    }

    /** @return An object representing the user with the given id. */
    public UserDomainObject getUser(int userId) {
        return getUserFromSqlRow(sqlSelectUserById(userId));
    }

    private String[][] sqlSelectAllUsers(boolean includeUserExtern, boolean includeInactiveUsers) {
        List whereTests = new ArrayList();
        if ( !includeUserExtern ) {
            whereTests.add("user_id != " + USER_EXTERN_ID);
        }
        if ( !includeInactiveUsers ) {
            whereTests.add("active = 1");
        }
        String sqlStr = SQL_SELECT_USERS;
        if ( whereTests.size() > 0 ) {
            sqlStr += " WHERE " + StringUtils.join(whereTests.iterator(), " AND ");
        }
        try {
            final Object[] parameters = new String[0];
            return (String[][]) services.getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    String[] sqlSelectUserById(int userId) {

        try {
            final Object[] parameters = new String[] { "" + userId };
            return (String[]) services.getDatabase().execute(new SqlQueryCommand(SQL__SELECT_USER_BY_ID, parameters, Utility.STRING_ARRAY_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public void saveUser(String loginName, UserDomainObject userToSave) {
        UserDomainObject imcmsUser = getUser(loginName);
        userToSave.setId(imcmsUser.getId());
        userToSave.setLoginName(loginName);
        saveUser(userToSave);
    }


    public void updateUserSessionId(final UserDomainObject loggedInUser) {
        services.getDatabase().execute(new SqlUpdateCommand(SQL_UPDATE_USER_SESSION,
                new Object[] { loggedInUser.getSessionId(), loggedInUser.getId() }
        ));
    }


    public String getUserSessionId(final UserDomainObject loggedInUser) {
        return (String)services.getDatabase().execute(new SqlQueryCommand(SQL_SELECT_USER_SESSION,
                new Object[] { loggedInUser.getId() },
                Utility.SINGLE_STRING_HANDLER
        ));
    }
    
    public void saveUser(UserDomainObject user) {

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
                user.getProvince(),
                user.getEmailAddress(),
                user.isImcmsExternal() ? "1" : "0",
                user.isActive() ? "1" : "0",
                user.getLanguageIso639_2(),
                "" + user.getId(),
        };
        try {
            String externalColumn = DatabaseUtils.isDatabaseMSSql() ? "[external]" : "external";
            services.getDatabase().execute(new SqlUpdateCommand("UPDATE users \n"
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
                                                                + externalColumn + " = ?,\n"
                                                                + "active = ?,\n"
                                                                + "language = ?\n"
                                                                + "WHERE user_id = ?", params));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }

        updateUserRoles(user);
        removePhoneNumbers(user);
        addPhoneNumbers(user);
    }

    private void updateUserRoles(UserDomainObject newUser) {
        Set<RoleId> newUserRoleIds = new HashSet(Arrays.asList(newUser.getRoleIds()));
        newUserRoleIds.add(RoleId.USERS);
        CompositeDatabaseCommand updateUserRolesCommand = new CompositeDatabaseCommand(new DeleteWhereColumnsEqualDatabaseCommand("user_roles_crossref", "user_id", new Integer(newUser.getId())));
        for ( RoleId roleId : newUserRoleIds ) {
            updateUserRolesCommand.add(new InsertIntoTableDatabaseCommand("user_roles_crossref", new String[][] {
                    { "user_id", "" + newUser.getId() },
                    { "role_id", "" + roleId.intValue() }
            }));
        }
        services.getDatabase().execute(updateUserRolesCommand);
        sqlUpdateUserUserAdminRoles(newUser);
    }

    private void sqlUpdateUserUserAdminRoles(UserDomainObject user) {
        DeleteWhereColumnsEqualDatabaseCommand deleteAllUserAdminRolesForUserCommand = new DeleteWhereColumnsEqualDatabaseCommand(TABLE__USERADMIN_ROLE_CROSSREF, "user_id",
                                                                                                                                  ""
                                                                                                                                  + user.getId());
        CompositeDatabaseCommand updateUserAdminRolesCommand = new CompositeDatabaseCommand(deleteAllUserAdminRolesForUserCommand);
        RoleId[] userAdminRolesReferences = user.getUserAdminRoleIds();
        for ( RoleId userAdminRoleId : userAdminRolesReferences ) {
            updateUserAdminRolesCommand.add(new InsertIntoTableDatabaseCommand(TABLE__USERADMIN_ROLE_CROSSREF, new String[][] {
                    { "user_id", "" + user.getId() },
                    { "role_id", "" + userAdminRoleId.intValue() }
            }));
        }
        services.getDatabase().execute(updateUserAdminRolesCommand);
    }

    public synchronized void addUser(UserDomainObject user) throws UserAlreadyExistsException {
        if ( null != getUser(user.getLoginName()) ) {
            throw new UserAlreadyExistsException(
                    "A user with the name \"" + user.getLoginName() + "\" already exists.");
        }
        try {
            if ( user.isImcmsExternal() ) {
                user.setPassword("");
            }
            String externalColumn = DatabaseUtils.isDatabaseMSSql() ? "[external]" : "external";
            Number newUserId = (Number) services.getDatabase().execute(new InsertIntoTableDatabaseCommand("users", new String[][] {
                    { "login_name", user.getLoginName() },
                    { "login_password", user.getPassword() },
                    { "first_name", user.getFirstName() },
                    { "last_name", user.getLastName() },
                    { "title", user.getTitle() },
                    { "company", user.getCompany() },
                    { "address", user.getAddress() },
                    { "city", user.getCity() },
                    { "zip", user.getZip() },
                    { "country", user.getCountry() },
                    { "county_council", user.getProvince() },
                    { "email", user.getEmailAddress() },
                    { externalColumn, user.isImcmsExternal() ? "1" : "0" },
                    { "active", user.isActive() ? "1" : "0" },
                    { "language", user.getLanguageIso639_2() },
                    { "create_date", Utility.makeSqlStringFromDate(new Date()) }
            }));
            int newIntUserId = newUserId.intValue();
            user.setId(newIntUserId);

            updateUserRoles(user);
            addPhoneNumbers(user);
        } catch ( IntegrityConstraintViolationException e ) {
            throw new UserAlreadyExistsException(e);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    private void removePhoneNumbers(UserDomainObject newUser) {
        String[] sprocParameters = new String[] { String.valueOf(newUser.getId()) };
        try {
            services.getProcedureExecutor().executeUpdateProcedure(SPROC_DEL_PHONE_NR, sprocParameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    private void addPhoneNumbers(UserDomainObject newUser) {
        CompositeDatabaseCommand addPhoneNumbersCommand = new CompositeDatabaseCommand();
        Set phoneNumbers = newUser.getPhoneNumbers();
        for ( Iterator iterator = phoneNumbers.iterator(); iterator.hasNext(); ) {
            PhoneNumber phoneNumber = (PhoneNumber) iterator.next();
            addPhoneNumbersCommand.add(new InsertIntoTableDatabaseCommand("phones", new String[][] {
                    { "user_id", "" + newUser.getId() },
                    { "number", phoneNumber.getNumber() },
                    { "phonetype_id", "" + phoneNumber.getType().getId() }
            }));
        }
        services.getDatabase().execute(addPhoneNumbersCommand);
    }

    /** @deprecated  */
    public String[] getRoleNames(UserDomainObject user) {
        try {
            final Object[] parameters = new String[] { "" + user.getId() };
            return (String[]) services.getProcedureExecutor().executeProcedure(SPROC_GET_USER_ROLES, parameters, new StringArrayResultSetHandler());
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public String[] getAllRoleNames() {
        try {
            final Object[] parameters = new String[] { };
            String[] roleNamesMinusUsers = (String[]) services.getProcedureExecutor().executeProcedure(SPROC_GET_ALL_ROLES, parameters, new StringArrayResultSetHandler());

            Set roleNamesSet = new HashSet();
            for ( int i = 0; i < roleNamesMinusUsers.length; i += 2 ) {
                String roleName = roleNamesMinusUsers[i + 1];
                roleNamesSet.add(roleName);
            }

            roleNamesSet.add(getRole(RoleId.USERS).getName());

            String[] roleNames = (String[]) roleNamesSet.toArray(new String[roleNamesSet.size()]);
            Arrays.sort(roleNames);

            return roleNames;
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public void addRoleNames(String[] externalRoleNames) {
        for ( int i = 0; i < externalRoleNames.length; i++ ) {
            String externalRoleName = externalRoleNames[i];
            addRole(externalRoleName);
        }
    }

    public UserDomainObject[] getUsers(boolean includeUserExtern, boolean includeInactiveUsers) {
        String[][] allUsersSqlResult = sqlSelectAllUsers(includeUserExtern, includeInactiveUsers);
        return getUsersFromSqlRows(allUsersSqlResult);
    }

    public UserDomainObject[] getAllUsersWithRole(RoleDomainObject role) {
        try {
            if ( null == role ) {
                return new UserDomainObject[] { };
            }
            final Object[] parameters = new String[] { "" + role.getId() };
            String[] usersWithRole = (String[]) services.getProcedureExecutor().executeProcedure(SPROC_GET_USERS_WHO_BELONGS_TO_ROLE, parameters, new StringArrayResultSetHandler());
            UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

            for ( int i = 0; i < result.length; i++ ) {
                String userIdStr = usersWithRole[i * 2];
                result[i] = getUser(Integer.parseInt(userIdStr));
            }
            return result;
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public synchronized RoleDomainObject addRole(String roleName) {
        RoleDomainObject role = getRoleByName(roleName);
        if ( null == role ) {
            role = new RoleDomainObject(roleName);
            try {
                addRole(role);
            } catch ( UserAndRoleRegistryException e ) {
                throw new UnhandledException(e);
            }
        }
        return role;
    }

    void addRole(final RoleDomainObject role) throws RoleAlreadyExistsException, NameTooLongException {
        try {
            final int unionOfPermissionSetIds = getUnionOfRolePermissionIds(role);
            final int newRoleId = ( (Number) services.getDatabase().execute(new TransactionDatabaseCommand() {
                public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                    return connection.executeUpdateAndGetGeneratedKey(SQL_INSERT_INTO_ROLES, new String[] {
                            role.getName(),
                            ""
                            + unionOfPermissionSetIds });
                }
            }) ).intValue();
            role.setId(new RoleId(newRoleId));
        } catch ( IntegrityConstraintViolationException icvse ) {
            throw new RoleAlreadyExistsException("A role with the name \"" + role.getName()
                                                 + "\" already exists.");
        } catch ( StringTruncationException stse ) {
            throw new NameTooLongException("Role name too long: " + role.getName());
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    private int getUnionOfRolePermissionIds(RoleDomainObject role) {
        int unionOfPermissionSetIds = 0;
        RolePermissionDomainObject[] rolePermissions = role.getPermissions();
        for ( int i = 0; i < rolePermissions.length; i++ ) {
            RolePermissionDomainObject rolePermission = rolePermissions[i];
            unionOfPermissionSetIds |= rolePermission.getId();
        }
        return unionOfPermissionSetIds;
    }

    public void deleteRole(RoleDomainObject role) {
        if ( null == role ) {
            return;
        }
        try {
            DatabaseCommand databaseCommand = new CompositeDatabaseCommand(new DatabaseCommand[] {
                    new DeleteWhereColumnsEqualDatabaseCommand("roles_rights", "role_id", "" + role.getId()),
                    new DeleteWhereColumnsEqualDatabaseCommand("user_roles_crossref", "role_id", "" + role.getId()),
                    new DeleteWhereColumnsEqualDatabaseCommand("roles", "role_id", "" + role.getId()),
            });
            services.getDatabase().execute(databaseCommand);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject[] getAllRoles() {
        return getRoles(SQL_SELECT_ALL_ROLES);
    }

    public RoleDomainObject[] getAllRolesExceptUsersRole() {
        return getRoles(SQL_SELECT_ALL_ROLES_EXCEPT_USERS_ROLE);
    }

    private RoleDomainObject[] getRoles(String rolesSql) {
        try {
            final Object[] parameters = new String[0];
            String[][] sqlRows = (String[][]) services.getDatabase().execute(new SqlQueryCommand(rolesSql, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
            RoleDomainObject[] roles = new RoleDomainObject[sqlRows.length];
            for ( int i = 0; i < sqlRows.length; i++ ) {
                roles[i] = getRoleFromSqlResult(sqlRows[i]);
            }
            return roles;
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleById(int roleId) {
        try {
            final Object[] parameters = new String[] { "" + roleId };
            String[] sqlResult = (String[]) services.getDatabase().execute(new SqlQueryCommand(SQL_SELECT_ROLE_BY_ID, parameters, Utility.STRING_ARRAY_HANDLER));
            return getRoleFromSqlResult(sqlResult);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleByName(String wantedRoleName) {
        try {
            final Object[] parameters = new String[] { wantedRoleName };
            String[] sqlResult = (String[]) services.getDatabase().execute(new SqlQueryCommand(SQL_SELECT_ROLE_BY_NAME, parameters, Utility.STRING_ARRAY_HANDLER));
            return getRoleFromSqlResult(sqlResult);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleFromSqlResult(String[] sqlResult) {
        RoleDomainObject role = null;
        if ( sqlResult.length > 0 ) {
            int roleId = Integer.parseInt(sqlResult[0]);
            String roleName = sqlResult[1];
            int adminRoleId = Integer.parseInt(sqlResult[2]);
            int unionOfRolePermissionIds = Integer.parseInt(sqlResult[3]);
            role = new RoleDomainObject(new RoleId(roleId), roleName, adminRoleId);
            role.addUnionOfPermissionIdsToRole(unionOfRolePermissionIds);
        }
        return role;
    }

    public UserDomainObject[] getAllUsers() {
        return getUsers(true, true);
    }

    public UserDomainObject[] findUsersByNamePrefix(String namePrefix, boolean includeInactiveUsers) {
        try {
            String sql = SQL_SELECT_USERS + " WHERE user_id != " + USER_EXTERN_ID
                         + " AND ( login_name LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR title LIKE ? OR email LIKE ? OR company LIKE ? )";
            if ( !includeInactiveUsers ) {
                sql += " AND active = 1";
            }
            sql += " ORDER BY last_name, first_name";
            String like = namePrefix + "%";
            final Object[] parameters = new String[] { like, like, like, like, like, like };
            String[][] sqlRows = (String[][]) services.getDatabase().execute(new SqlQueryCommand(sql, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
            return getUsersFromSqlRows(sqlRows);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    private UserDomainObject[] getUsersFromSqlRows(String[][] sqlRows) {
        UserDomainObject[] users = new UserDomainObject[sqlRows.length];
        for ( int i = 0; i < sqlRows.length; i++ ) {
            users[i] = getUserFromSqlRow(sqlRows[i]);
        }
        return users;
    }

    public void initUserPhoneNumbers(UserDomainObject user) {
        PhoneNumber[] phoneNbr = getUserPhoneNumbers(user.getId());
        for ( int i = 0; i < phoneNbr.length; i++ ) {
            PhoneNumberType type = phoneNbr[i].getType();
            String number = phoneNbr[i].getNumber();

            user.addPhoneNumber(new PhoneNumber(number, type));
        }
    }

    public void initUserRoles(UserDomainObject user) {
        user.setRoleIds(getRoleReferencesForUser(user));
    }

    public void initUserUserAdminRoles(UserDomainObject user) {
        user.setUserAdminRolesIds(getUserAdminRolesReferencesForUser(user));
    }

    public void saveRole(RoleDomainObject role) throws NameTooLongException, RoleAlreadyExistsException {
        if ( 0 == role.getId().intValue() ) {
            addRole(role);
        } else {
            saveExistingRole(role);
        }
    }

    private void saveExistingRole(RoleDomainObject role) {
        int unionOfRolePermissionIds = getUnionOfRolePermissionIds(role);
        try {
            final Object[] parameters = new String[] {
                    role.getName(),
                    "" + unionOfRolePermissionIds, "" + role.getId() };
            services.getDatabase().execute(new SqlUpdateCommand("UPDATE roles SET role_name = ?, permissions = ? WHERE role_id = ?", parameters));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public PhoneNumber[] getUserPhoneNumbers(int userToChangeId) {
        try {
            final Object[] parameters = new String[] {
                    "" + userToChangeId };
            String[][] phoneNumberData = (String[][]) services.getDatabase().execute(new SqlQueryCommand(
                    "SELECT   phones.number, phones.phonetype_id\n"
                    + "FROM   phones\n"
                    + "WHERE  phones.user_id = ?", parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
            List phoneNumbers = new ArrayList();
            for ( int i = 0; i < phoneNumberData.length; i++ ) {
                String[] row = phoneNumberData[i];
                String phoneNumberString = row[0];
                int phoneTypeId = Integer.parseInt(row[1]);
                PhoneNumberType phoneNumberType = PhoneNumberType.getPhoneNumberTypeById(phoneTypeId);
                PhoneNumber phoneNumber = new PhoneNumber(phoneNumberString, phoneNumberType);
                phoneNumbers.add(phoneNumber);
            }
            return (PhoneNumber[]) phoneNumbers.toArray(new PhoneNumber[phoneNumbers.size()]);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    private RoleId[] getUserAdminRolesReferencesForUser(UserDomainObject loggedOnUser) {
        try {
            final Object[] parameters = new String[] {
                    "" + loggedOnUser.getId() };
            String[] roleIds = (String[]) services.getDatabase().execute(new SqlQueryCommand("SELECT role_id\n"
                                                                                             + "FROM useradmin_role_crossref\n"
                                                                                             + "WHERE user_id = ?", parameters, Utility.STRING_ARRAY_HANDLER));

            List useradminPermissibleRolesList = new ArrayList(roleIds.length);
            for ( int i = 0; i < roleIds.length; i++ ) {
                useradminPermissibleRolesList.add(new RoleId(Integer.parseInt(roleIds[i])));
            }
            return (RoleId[]) useradminPermissibleRolesList.toArray(new RoleId[useradminPermissibleRolesList.size()]);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public UserDomainObject getDefaultUser() {
        return getUser(UserDomainObject.DEFAULT_USER_ID);
    }

    public RoleDomainObject getRole(RoleId roleId) {
        return getRoleById(roleId.intValue());
    }

    public UserDomainObject getUserByIpAddress(String ipAddress) {

        long ip;
        try {
            ip = Utility.ipStringToLong(ipAddress);
        } catch ( IllegalArgumentException nfe ) {
            log.debug("Failed to parse ip address " + ipAddress);
            return null;
        }

        String sqlStr = "select users.user_id from users,ip_accesses"
                        + " where users.user_id = ip_accesses.user_id"
                        + " and ip_accesses.ip_start <= ?"
                        + " and ip_accesses.ip_end >= ?";

        final Object[] parameters = new String[] { "" + ip,
                "" + ip };
        String userIdString = (String) services.getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.SINGLE_STRING_HANDLER));

        if ( null != userIdString ) {
            return getUser(Integer.parseInt(userIdString));
        }
        return null;
    }

}