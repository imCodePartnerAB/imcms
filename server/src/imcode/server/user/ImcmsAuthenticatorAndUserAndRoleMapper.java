package imcode.server.user;

import imcode.server.LanguageMapper;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.commands.CompositeDatabaseCommand;
import imcode.server.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import imcode.server.db.commands.InsertIntoTableDatabaseCommand;
import imcode.server.db.commands.TransactionDatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.exceptions.IntegrityConstraintViolationException;
import imcode.server.db.exceptions.StringTruncationException;
import imcode.util.DateConstants;
import imcode.util.Utility;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImcmsAuthenticatorAndUserAndRoleMapper implements UserAndRoleRegistry, Authenticator, RoleGetter {

    private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
    private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";

    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";

    private static final int USER_EXTERN_ID = 2;

    private final Database database;
    private static final String SQL_SELECT_USERS = "SELECT user_id, login_name, login_password, first_name, last_name, "
                                                   + "title, company, address, city, zip, country, county_council, "
                                                   + "email, language, active, "
                                                   + "create_date, external "
                                                   + "FROM users";

    public static final String SQL_ROLES_COLUMNS = "roles.role_id, roles.role_name, roles.admin_role, roles.permissions";
    private static final String SQL_SELECT_ALL_ROLES = "SELECT " + SQL_ROLES_COLUMNS + " FROM roles";
    private static final String SQL_SELECT_ALL_ROLES_EXCEPT_USERS_ROLE = SQL_SELECT_ALL_ROLES
                                                                         + " WHERE roles.role_id != " + RoleId
            .USERS_ID;

    public static final String SQL_SELECT_ROLE_BY_NAME = SQL_SELECT_ALL_ROLES + " WHERE role_name = ?";
    private static final String SQL_SELECT_ROLE_BY_ID = SQL_SELECT_ALL_ROLES + " WHERE role_id = ?";

    public static final String SQL_INSERT_INTO_ROLES = "INSERT INTO roles (role_name, permissions, admin_role) VALUES(?,?,0)";
    private LanguageMapper languageMapper;
    private static final String TABLE__USERADMIN_ROLE_CROSSREF = "useradmin_role_crossref";

    public ImcmsAuthenticatorAndUserAndRoleMapper(Database database,
                                                  LanguageMapper languageMapper) {
        this.database = database;
        this.languageMapper = languageMapper;
    }

    public boolean authenticate(String loginName, String password) {
        boolean userExistsAndPasswordIsCorrect = false;
        UserDomainObject user = getUser(loginName);
        if ( null != user ) {
            String password_from_db = user.getPassword();

            if ( password_from_db.equals(password) && user.isActive() ) {
                userExistsAndPasswordIsCorrect = true;
            } else if ( !user.isActive() ) {
                userExistsAndPasswordIsCorrect = false;
            } else {
                userExistsAndPasswordIsCorrect = false;
            }
        }

        return userExistsAndPasswordIsCorrect;
    }

    public UserDomainObject getUser(String loginName) {
        return getUserFromSqlRow(sqlSelectUserByName(loginName));
    }

    private String[] sqlSelectUserByName(String loginName) {
        return database.executeArrayQuery(SQL_SELECT_USERS
                                          + " WHERE login_name = ?",
                                          new String[] { loginName.trim() });
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
        user.setDistrict(sqlResult[11]);
        user.setEmailAddress(sqlResult[12]);
        user.setLanguageIso639_2((String) ObjectUtils.defaultIfNull(sqlResult[13], languageMapper.getDefaultLanguage()));
        user.setActive(0 != Integer.parseInt(sqlResult[14]));
        DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        user.setCreateDate(Utility.parseDateFormat(dateFormat, sqlResult[15]));
        user.setImcmsExternal(0 != Integer.parseInt(sqlResult[16]));
    }

    private RoleId[] getRoleReferencesForUser(UserDomainObject user) {
        try {
            String sqlStr = SQL_SELECT_ALL_ROLES + ", user_roles_crossref"
                            + " WHERE user_roles_crossref.role_id = roles.role_id"
                            + " AND user_roles_crossref.user_id = ?";
            String[][] sqlResult = database.execute2dArrayQuery(sqlStr, new String[] { "" + user.getId() });
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

    private RoleId getRoleReferenceFromSqlResult(String[] sqlRow) {
        return new RoleId(Integer.parseInt(sqlRow[0])) ;
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
            return database.execute2dArrayQuery(sqlStr, new String[0]);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    String[] sqlSelectUserById(int userId) {
        String sqlStr = SQL_SELECT_USERS
                        + " WHERE user_id = ?";

        try {
            return database.executeArrayQuery(sqlStr, new String[] { "" + userId });
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public void saveUser(String loginName, UserDomainObject userToSave, UserDomainObject currentUser) {
        UserDomainObject imcmsUser = getUser(loginName);
        userToSave.setId(imcmsUser.getId());
        userToSave.setLoginName(loginName);
        saveUser(userToSave, currentUser);
    }

    public void saveUser(UserDomainObject user, UserDomainObject currentUser) {
        UserDomainObject oldUser = getUser(user.getId());

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
                user.getDistrict(),
                user.getEmailAddress(),
                user.isImcmsExternal() ? "1" : "0",
                user.isActive() ? "1" : "0",
                user.getLanguageIso639_2(),
                "" + user.getId(),
        };
        try {
            database.executeUpdateQuery("UPDATE users \n"
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
                                        + "WHERE user_id = ?", params);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }

        updateUserRoles(user, oldUser, currentUser);
        removePhoneNumbers(user);
        addPhoneNumbers(user);
    }

    private void updateUserRoles(UserDomainObject newUser, UserDomainObject oldUser, UserDomainObject loggedInUser) {
        if ( !newUser.equals(loggedInUser) ) {
            Set newUserRoles = new HashSet(Arrays.asList(newUser.getRoleIds()));
            if ( null != loggedInUser && loggedInUser.isUserAdminOnly() ) {
                Set loggedInUserUserAdminRoles = new HashSet(Arrays.asList(loggedInUser.getUserAdminRoleIds()));
                newUserRoles.retainAll(loggedInUserUserAdminRoles);
                if ( null != oldUser ) {
                    Set oldUserRoles = new HashSet(Arrays.asList(oldUser.getRoleIds()));
                    oldUserRoles.removeAll(loggedInUserUserAdminRoles);
                    newUserRoles.addAll(oldUserRoles);
                }
            }
            newUserRoles.add(RoleId.USERS);
            CompositeDatabaseCommand updateUserRolesCommand = new CompositeDatabaseCommand(new DeleteWhereColumnsEqualDatabaseCommand("user_roles_crossref", "user_id", new Integer(newUser.getId())));
            for ( Iterator iterator = newUserRoles.iterator(); iterator.hasNext(); ) {
                RoleId roleId = (RoleId) iterator.next();
                updateUserRolesCommand.add(new InsertIntoTableDatabaseCommand("user_roles_crossref", new String[][] {
                        { "user_id", "" + newUser.getId() },
                        { "role_id", "" + roleId.intValue() }
                }));
            }
            database.executeCommand(updateUserRolesCommand);
            if ( null == loggedInUser || loggedInUser.isSuperAdmin() ) {
                sqlUpdateUserUserAdminRoles(newUser);
            }
        }
    }

    private void sqlUpdateUserUserAdminRoles(UserDomainObject user) {
        DeleteWhereColumnsEqualDatabaseCommand deleteAllUserAdminRolesForUserCommand = new DeleteWhereColumnsEqualDatabaseCommand(TABLE__USERADMIN_ROLE_CROSSREF, "user_id",
                                                                                                                                  ""
                                                                                                                                  + user.getId());
        CompositeDatabaseCommand updateUserAdminRolesCommand = new CompositeDatabaseCommand(deleteAllUserAdminRolesForUserCommand);
        RoleId[] userAdminRolesReferences = user.getUserAdminRoleIds();
        for ( int i = 0; i < userAdminRolesReferences.length; i++ ) {
            RoleId userAdminRoleId = userAdminRolesReferences[i];
            updateUserAdminRolesCommand.add(new InsertIntoTableDatabaseCommand(TABLE__USERADMIN_ROLE_CROSSREF, new String[][] {
                    { "user_id", "" + user.getId() },
                    { "role_id", "" + userAdminRoleId.intValue() }
            }));
        }
        database.executeCommand(updateUserAdminRolesCommand);
    }

    public synchronized void addUser(UserDomainObject user,
                                     UserDomainObject currentUser) throws UserAlreadyExistsException {
        if ( null != getUser(user.getLoginName()) ) {
            throw new UserAlreadyExistsException(
                    "A user with the name \"" + user.getLoginName() + "\" already exists.");
        }
        try {
            if ( user.isImcmsExternal() ) {
                user.setPassword("");
            }
            Number newUserId = (Number) database.executeCommand(new InsertIntoTableDatabaseCommand("users", new String[][] {
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
                    { "county_council", user.getDistrict() },
                    { "email", user.getEmailAddress() },
                    { "external", user.isImcmsExternal() ? "1" : "0" },
                    { "active", user.isActive() ? "1" : "0" },
                    { "language", user.getLanguageIso639_2() },
                    { "create_date", Utility.makeSqlStringFromDate(new Date()) }
            }));
            int newIntUserId = newUserId.intValue();
            user.setId(newIntUserId);

            updateUserRoles(user, null, currentUser);
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
            database.executeUpdateProcedure(SPROC_DEL_PHONE_NR, sprocParameters);
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
        database.executeCommand(addPhoneNumbersCommand);
    }

    /** @deprecated  */
    public String[] getRoleNames(UserDomainObject user) {
        try {
            return database.executeArrayProcedure(SPROC_GET_USER_ROLES, new String[] { "" + user.getId() });
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public String[] getAllRoleNames() {
        try {
            String[] roleNamesMinusUsers = database.executeArrayProcedure(SPROC_GET_ALL_ROLES, new String[] { });

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
            this.addRole(externalRoleName);
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
            String[] usersWithRole = database.executeArrayProcedure(SPROC_GET_USERS_WHO_BELONGS_TO_ROLE,
                                                                    new String[] { "" + role.getId() });
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

    public void addPhoneNumber(final int newUserId, final String phoneNumber, final int phoneNumberType
    ) {
        try {
            database.executeCommand(new InsertIntoTableDatabaseCommand("phones", new String[][] {
                    { "user_id", "" + newUserId },
                    { "number", phoneNumber },
                    { "phonetype_id", "" + phoneNumberType }
            }));
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
            final int newRoleId;
            newRoleId = ( (Number) database.executeCommand(new TransactionDatabaseCommand() {
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
            database.executeCommand(databaseCommand);
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
            String[][] sqlRows = database.execute2dArrayQuery(rolesSql, new String[0]);
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
            String[] sqlResult = database.executeArrayQuery(SQL_SELECT_ROLE_BY_ID, new String[] { "" + roleId });
            return getRoleFromSqlResult(sqlResult);
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleByName(String wantedRoleName) {
        try {
            String[] sqlResult = database.executeArrayQuery(SQL_SELECT_ROLE_BY_NAME, new String[] { wantedRoleName });
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
                         + " AND ( login_name LIKE ? OR first_name LIKE ? OR last_name LIKE ? )";
            if ( !includeInactiveUsers ) {
                sql += " AND active = 1";
            }
            sql += " ORDER BY last_name, first_name";
            String like = namePrefix + "%";
            String[][] sqlRows = database.execute2dArrayQuery(sql, new String[] { like, like, like });
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
            database.executeUpdateQuery("UPDATE roles SET role_name = ?, permissions = ? WHERE role_id = ?", new String[] {
                    role.getName(),
                    "" + unionOfRolePermissionIds, "" + role.getId() });
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
    }

    public PhoneNumber[] getUserPhoneNumbers(int userToChangeId) {
        try {
            String[][] phoneNumberData = database.execute2dArrayQuery(
                    "SELECT   phones.number, phones.phonetype_id\n"
                    + "FROM   phones\n"
                    + "WHERE  phones.user_id = ?", new String[] {
                    "" + userToChangeId });
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

    public RoleId[] getUserAdminRolesReferencesForUser(UserDomainObject loggedOnUser) {
        try {
            String[] roleIds = database.executeArrayQuery("SELECT role_id\n"
                                                          + "FROM useradmin_role_crossref\n"
                                                          + "WHERE user_id = ?", new String[] {
                    "" + loggedOnUser.getId() });

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
        return getRoleById(roleId.intValue()) ;
    }
}