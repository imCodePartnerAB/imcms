package imcode.server.user;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.CompositeDatabaseCommand;
import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.db.exceptions.IntegrityConstraintViolationException;
import com.imcode.db.exceptions.StringTruncationException;
import com.imcode.imcms.api.UserAlreadyExistsException;
import com.imcode.imcms.db.StringArrayResultSetHandler;
import com.imcode.imcms.domain.service.IpAccessRuleService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserRepository;
import com.imcode.imcms.servlet.LoginPasswordManager;
import imcode.server.ImcmsServices;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ImcmsAuthenticatorAndUserAndRoleMapper implements UserAndRoleRegistry, Authenticator, RoleGetter {

    public static final String SQL_ROLES_COLUMNS = "roles.role_id, roles.role_name, roles.admin_role, roles.permissions";
    public static final String SQL_INSERT_INTO_ROLES = "INSERT INTO roles (role_name, permissions, admin_role) VALUES(?,?,0)";
    private final static Logger log = Logger.getLogger(ImcmsAuthenticatorAndUserAndRoleMapper.class);
    private static final String SPROC_GET_ALL_ROLES = "GetAllRoles";
    private static final String SPROC_GET_USER_ROLES = "GetUserRoles";
    private static final String SPROC_GET_USERS_WHO_BELONGS_TO_ROLE = "GetUsersWhoBelongsToRole";
    private static final String SPROC_DEL_PHONE_NR = "DelPhoneNr";
    private static final String SQL_SELECT_ALL_ROLES = "SELECT " + SQL_ROLES_COLUMNS + " FROM roles";
    public static final String SQL_SELECT_ROLE_BY_NAME = SQL_SELECT_ALL_ROLES + " WHERE role_name = ?";
    private static final String SQL_SELECT_ALL_ROLES_EXCEPT_USERS_ROLE = SQL_SELECT_ALL_ROLES
            + " WHERE roles.role_id != " + Roles.USER.getId();
    private static final String SQL_SELECT_ROLE_BY_ID = SQL_SELECT_ALL_ROLES + " WHERE role_id = ?";
    private static final String TABLE__USERADMIN_ROLE_CROSSREF = "useradmin_role_crossref";

    private final ImcmsServices services;
    private final UserService userService;
    private final IpAccessRuleService ipAccessRuleService;

    private UserRepository userRepository;

    /**
     * @since 4.0.7
     */
    private LoginPasswordManager loginPasswordManager;

    public ImcmsAuthenticatorAndUserAndRoleMapper(ImcmsServices services, LoginPasswordManager userLoginPasswordManager) {
        this.services = services;
        this.loginPasswordManager = userLoginPasswordManager;
        this.userRepository = services.getManagedBean(UserRepository.class);
        this.userService = services.getManagedBean(UserService.class);
        this.ipAccessRuleService = services.getManagedBean(IpAccessRuleService.class);
    }

    /**
     * Authenticates internal user.
     * The user being authenticated must be internal and his account must be active.
     *
     * @param loginName user's login name
     * @param password  user's plain text or encrypted password
     * @return if user has been authenticated.
     */
    public boolean authenticate(String loginName, String password) {
        UserDomainObject user = getUser(loginName);

        return ((user != null)
                && user.isActive()
                && !user.isImcmsExternal()
                && (user.isPasswordEncrypted()
                ? loginPasswordManager.validatePassword(password, user.getPassword())
                : password.equals(user.getPassword()))
        );
    }

    /**
     * @since 4.0.7
     */
    public UserDomainObject getUserByEmail(String email) {
        return email == null ? null : toDomainObject(userRepository.findByEmailUnique(email));
    }

    /**
     * Create and assign a new PasswordReset to the existing user.
     * User must exist and must not be default, external or superadmin when password reset is not allowed for superadmins.
     *
     * @param id existing user's id.
     * @return user with assigned PasswordReset
     * @throws IllegalStateException if PasswordReset can not be created.
     * @since 4.1.3
     */
    public UserDomainObject createPasswordReset(int id) {
        UserDomainObject user = getUser(id);

        if (user == null)
            throw new IllegalStateException(String.format("User with id %s does not exist.", id));

        String illegalState = user.isDefaultUser()
                ? "default"
                : user.isImcmsExternal()
                ? "external"
                : user.isSuperAdmin() && !services.getConfig().isSuperadminLoginPasswordResetAllowed()
                ? "superuser"
                : null;

        if (illegalState != null)
            throw new IllegalStateException(String.format(
                    "Can't create password reset for [%s] user %s. User must not be default, external or superadmin when password reset is not allowed for superadmins.",
                    illegalState, user));

        user.setPasswordReset(UUID.randomUUID().toString(), System.currentTimeMillis());
        saveUser(user);

        return user;
    }


    /**
     * @param resetId password reset id
     * @return user or null if user can not be found, password-reset has been expired, or user is superadmin when password reset is not allowed for superadmins.
     * @since 4.1.3
     */
    public UserDomainObject getUserByPasswordResetId(String resetId) {
        UserDomainObject user = toDomainObject(userRepository.findByPasswordResetId(resetId));

        return ((user == null)
                || isPasswordResetExpired(user.getPasswordReset().getTime())
                || (user.isSuperAdmin() && !services.getConfig().isSuperadminLoginPasswordResetAllowed()))
                ? null
                : user;
    }

    public UserDomainObject getUser(String loginName) {
        return StringUtils.isBlank(loginName) ? null : toDomainObject(userRepository.findByLogin(loginName));
    }

    public UserDomainObject getActiveUser(String loginName) {
        return StringUtils.isBlank(loginName) ? null : toDomainObject(userRepository.findByLoginAndActiveIsTrue(loginName));
    }

    /**
     * @param login login
     * @return user or null if user can not be found.
     * @since 4.1.3
     */
    public UserDomainObject getUserByLoginIgnoreCase(String login) {
        return StringUtils.isBlank(login) ? null : toDomainObject(userRepository.findByLoginIgnoreCase(login));
    }

    /**
     * @param time password reset time
     * @return if password reset has been expired.
     * @since 4.0.7
     */
    private boolean isPasswordResetExpired(long time) {
        int interval = Hours.hoursBetween(new DateTime(time), new DateTime()).getHours();

        return interval > services.getSystemData().getUserLoginPasswordResetExpirationInterval();
    }

    private UserDomainObject toDomainObject(User user) {
        if (user == null) {
            return null;
        }

        UserDomainObject userDO = new UserDomainObject();
        userDO.setActive(user.isActive());
        userDO.setAddress(user.getAddress());
        //userDO.setAuthenticatedByIp();
        userDO.setCity(user.getCity());
        userDO.setCompany(user.getCompany());
        userDO.setCountry(user.getCountry());
        //todo: check date
        //DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
        userDO.setCreateDate(user.getCreateDate());
        //userDO.setCurrentContextPath();
        userDO.setEmailAddress(user.getEmail());
        userDO.setFirstName(user.getFirstName());
        userDO.setId(user.getId());
        userDO.setLoginName(user.getLogin());
        userDO.setImcmsExternal(user.isExternal());
        userDO.setLanguageIso639_2(services.getLanguageMapper().getDefaultLanguage());
        userDO.setLastName(user.getLastName());
        userDO.setPassword(user.getPassword(), UserDomainObject.PasswordType.valueOf(user.getPasswordType().name()));
        Optional.ofNullable(user.getPasswordReset()).ifPresent(passwordReset ->
                userDO.setPasswordReset(passwordReset.getId(), passwordReset.getTimestamp())
        );
        userDO.setProvince(user.getProvince());
        //userDO.setRoleIds();
        userDO.setSessionId(user.getSessionId());
        //userDO.setTemplateGroup();
        userDO.setTitle(user.getTitle());
        //userDO.setUserAdminRolesIds();
        userDO.setZip(user.getZip());

        initUserRoles(userDO);
        initUserPhoneNumbers(userDO);
        initUserUserAdminRoles(userDO);

        return userDO;
    }

    private Set<Integer> getRoleReferencesForUser(UserDomainObject user) {
        try {
            final String sqlStr = SQL_SELECT_ALL_ROLES + ", user_roles_crossref"
                    + " WHERE user_roles_crossref.role_id = roles.role_id"
                    + " AND user_roles_crossref.user_id = ?";

            final Object[] parameters = new String[]{"" + user.getId()};

            final String[][] sqlResult = services.getDatabase().execute(new SqlQueryCommand<>(
                    sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER
            ));
            final Set<Integer> roleReferences = new HashSet<>(sqlResult.length);

            for (String[] sqlRow : sqlResult) {
                roleReferences.add(Integer.parseInt(sqlRow[0]));
            }

            return roleReferences;

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    private Integer getRoleReferenceFromSqlResult(String[] sqlRow) {
        return Integer.parseInt(sqlRow[0]);
    }

    /**
     * @return An object representing the user with the given id.
     */
    public UserDomainObject getUser(int userId) {
        return toDomainObject(userRepository.findById(userId));
    }


    public void saveUser(String loginName, UserDomainObject userToSave) {
        UserDomainObject imcmsUser = getUser(loginName);
        userToSave.setId(imcmsUser.getId());
        userToSave.setLoginName(loginName);
        saveUser(userToSave);
    }


    public void updateUserSessionId(final UserDomainObject loggedInUser) {
        userRepository.updateSessionId(loggedInUser.getId(), loggedInUser.getSessionId());
    }


    public String getUserSessionId(final UserDomainObject loggedInUser) {
        return userRepository.findSessionId(loggedInUser.getId());
    }

    /**
     * If user is external sets the password to blank.
     * Otherwise encrypts the password if it was modified.
     *
     * @param user user
     * @since 4.0.7
     */
    private void modifyPasswordIfNecessary(UserDomainObject user) {
        if (user.isImcmsExternal()) {
            user.setPassword("");

        } else if (!user.isPasswordEncrypted() && services.getConfig().isLoginPasswordEncryptionEnabled()) {

            String password = user.getPassword();

            if (StringUtils.isNotBlank(password)) {
                user.setPassword(loginPasswordManager.encryptPassword(password), UserDomainObject.PasswordType.ENCRYPTED);
            }
        }
    }

    public void saveUser(UserDomainObject user) {
        modifyPasswordIfNecessary(user);

        String[] params = {
                getSafeTrimmedString(user.getLoginName()),
                (null == user.getPassword()) ? "" : user.getPassword().trim(),
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
                user.isPasswordEncrypted() ? "1" : "0",
                user.hasPasswordReset() ? user.getPasswordReset().getId() : null,
                user.hasPasswordReset() ? Long.toString(user.getPasswordReset().getTime()) : null,
                "" + user.getId(),
        };
        try {
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
                    + "external = ?,\n"
                    + "active = ?,\n"
                    + "language = ?,\n"
                    + "login_password_is_encrypted = ?,\n"
                    + "login_password_reset_id = ?,\n"
                    + "login_password_reset_ts = ?\n"
                    + "WHERE user_id = ?", params));
        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }

        updateUserRoles(user);
        removePhoneNumbers(user);
        addPhoneNumbers(user);
    }

    private void updateUserRoles(UserDomainObject newUser) {
        Set<Integer> newUserRoleIds = new HashSet<>(newUser.getRoleIds());
        newUserRoleIds.add(Roles.USER.getId());

        CompositeDatabaseCommand updateUserRolesCommand = new CompositeDatabaseCommand(
                new DeleteWhereColumnsEqualDatabaseCommand(
                        "user_roles_crossref", "user_id", newUser.getId()
                )
        );
        for (Integer roleId : newUserRoleIds) {
            updateUserRolesCommand.add(new InsertIntoTableDatabaseCommand("user_roles_crossref", new String[][]{
                    {"user_id", "" + newUser.getId()},
                    {"role_id", "" + roleId}
            }));
        }
        services.getDatabase().execute(updateUserRolesCommand);
        sqlUpdateUserUserAdminRoles(newUser);
    }

    private void sqlUpdateUserUserAdminRoles(UserDomainObject user) {
        final CompositeDatabaseCommand updateUserAdminRolesCommand = new CompositeDatabaseCommand(
                new DeleteWhereColumnsEqualDatabaseCommand(
                        TABLE__USERADMIN_ROLE_CROSSREF, "user_id", "" + user.getId()
                )
        );

        for (Integer userAdminRoleId : user.getUserAdminRoleIds()) {
            updateUserAdminRolesCommand.add(new InsertIntoTableDatabaseCommand(TABLE__USERADMIN_ROLE_CROSSREF, new String[][]{
                    {"user_id", "" + user.getId()},
                    {"role_id", "" + userAdminRoleId}
            }));
        }
        services.getDatabase().execute(updateUserAdminRolesCommand);
    }

    private String getSafeTrimmedString(String notSafeString) {
        return (notSafeString == null) ? null : notSafeString.trim();
    }

    public synchronized void addUser(UserDomainObject user) throws UserAlreadyExistsException {
        if (null != getActiveUser(user.getLoginName())) {
            throw new UserAlreadyExistsException(
                    "A user with the name \"" + user.getLoginName() + "\" already exists.");
        }
        try {

            UserDomainObject deactivatedUser = getUser(user.getLoginName());

            if (null != deactivatedUser && !deactivatedUser.isActive()) {
                deactivatedUser.setLoginName(deactivatedUser.getLoginName() + "_" + System.currentTimeMillis());
                deactivatedUser.setEmailAddress(deactivatedUser.getEmailAddress() + "_" + System.currentTimeMillis());
                saveUser(deactivatedUser);
            }
            modifyPasswordIfNecessary(user);

            Number newUserId = services.getDatabase().execute(new InsertIntoTableDatabaseCommand("users", new String[][]{
                    {"login_name", getSafeTrimmedString(user.getLoginName())},
                    {"login_password", getSafeTrimmedString(user.getPassword())},
                    {"first_name", user.getFirstName()},
                    {"last_name", user.getLastName()},
                    {"title", user.getTitle()},
                    {"company", user.getCompany()},
                    {"address", user.getAddress()},
                    {"city", user.getCity()},
                    {"zip", user.getZip()},
                    {"country", user.getCountry()},
                    {"county_council", user.getProvince()},
                    {"email", user.getEmailAddress()},
                    {"external", user.isImcmsExternal() ? "1" : "0"},
                    {"active", user.isActive() ? "1" : "0"},
                    {"language", user.getLanguageIso639_2()},
                    {"create_date", Utility.makeSqlStringFromDate(new Date())},

                    {"login_password_is_encrypted", BooleanUtils.toString(user.isPasswordEncrypted(), "1", "0")},
                    {"login_password_reset_id", user.hasPasswordReset() ? user.getPasswordReset().getId() : null},
                    {"login_password_reset_ts", user.hasPasswordReset() ? Long.toString(user.getPasswordReset().getTime()) : null}
            }));
            int newIntUserId = newUserId.intValue();
            user.setId(newIntUserId);

            updateUserRoles(user);
            addPhoneNumbers(user);

        } catch (IntegrityConstraintViolationException e) {
            throw new UserAlreadyExistsException(e);

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    private void removePhoneNumbers(UserDomainObject newUser) {
        String[] sprocParameters = new String[]{String.valueOf(newUser.getId())};

        try {
            services.getProcedureExecutor().executeUpdateProcedure(SPROC_DEL_PHONE_NR, sprocParameters);

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    private void addPhoneNumbers(UserDomainObject newUser) {
        CompositeDatabaseCommand addPhoneNumbersCommand = new CompositeDatabaseCommand();
        Set<PhoneNumber> phoneNumbers = newUser.getPhoneNumbers();

        for (PhoneNumber phoneNumber : phoneNumbers) {
            addPhoneNumbersCommand.add(new InsertIntoTableDatabaseCommand("phones", new String[][]{
                    {"user_id", "" + newUser.getId()},
                    {"number", phoneNumber.getNumber()},
                    {"phonetype_id", "" + phoneNumber.getType().getId()}
            }));
        }
        services.getDatabase().execute(addPhoneNumbersCommand);
    }

    /**
     * @deprecated
     */
    public String[] getRoleNames(UserDomainObject user) {
        try {
            final Object[] parameters = new String[]{"" + user.getId()};

            return services.getProcedureExecutor().executeProcedure(
                    SPROC_GET_USER_ROLES, parameters, new StringArrayResultSetHandler()
            );
        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public String[] getAllRoleNames() {
        try {
            String[] roleNamesMinusUsers = services.getProcedureExecutor().executeProcedure(
                    SPROC_GET_ALL_ROLES, new String[]{}, new StringArrayResultSetHandler()
            );

            Set<String> roleNamesSet = new HashSet<>();

            for (int i = 0; i < roleNamesMinusUsers.length; i += 2) {
                String roleName = roleNamesMinusUsers[i + 1];
                roleNamesSet.add(roleName);
            }

            roleNamesSet.add(getRole(Roles.USER.getId()).getName());

            String[] roleNames = roleNamesSet.toArray(new String[0]);
            Arrays.sort(roleNames);

            return roleNames;

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public void addRoleNames(String[] externalRoleNames) {
        for (String externalRoleName : externalRoleNames) {
            addRole(externalRoleName);
        }
    }

    public UserDomainObject[] getUsers(boolean includeUserExtern, boolean includeInactiveUsers) {
        return userService.findAll(includeUserExtern, includeInactiveUsers)
                .stream()
                .map(this::toDomainObject)
                .toArray(UserDomainObject[]::new);
    }

    /**
     * @since 4.1.3
     */
    public UserDomainObject[] getUsersByEmail(String email) {
        return userRepository.findByEmail(email)
                .stream()
                .map(this::toDomainObject)
                .toArray(UserDomainObject[]::new);
    }

    public UserDomainObject[] getAllUsersWithRole(RoleDomainObject role) {
        try {
            if (null == role) {
                return new UserDomainObject[]{};
            }
            final Object[] parameters = new String[]{"" + role.getId()};

            String[] usersWithRole = services.getProcedureExecutor().executeProcedure(
                    SPROC_GET_USERS_WHO_BELONGS_TO_ROLE, parameters, new StringArrayResultSetHandler()
            );
            UserDomainObject[] result = new UserDomainObject[usersWithRole.length / 2];

            for (int i = 0; i < result.length; i++) {
                String userIdStr = usersWithRole[i * 2];
                result[i] = getUser(Integer.parseInt(userIdStr));
            }
            return result;

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public synchronized RoleDomainObject addRole(String roleName) {
        RoleDomainObject role = getRoleByName(roleName);

        if (null == role) {
            role = new RoleDomainObject(roleName);
            try {
                addRole(role);
            } catch (UserAndRoleRegistryException e) {
                throw new UnhandledException(e);
            }
        }
        return role;
    }

    void addRole(final RoleDomainObject role) throws RoleAlreadyExistsException, NameTooLongException {
        try {
            final int unionOfPermissionSetIds = getUnionOfRolePermissionIds(role);

            final int newRoleId = services.getDatabase().execute(new TransactionDatabaseCommand<Number>() {
                public Number executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                    return connection.executeUpdateAndGetGeneratedKey(
                            SQL_INSERT_INTO_ROLES, new String[]{role.getName(), "" + unionOfPermissionSetIds}
                    );
                }
            }).intValue();
            role.setId(newRoleId);

        } catch (IntegrityConstraintViolationException icvse) {
            throw new RoleAlreadyExistsException("A role with the name \"" + role.getName() + "\" already exists.");

        } catch (StringTruncationException stse) {
            throw new NameTooLongException("Role name too long: " + role.getName());

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    private int getUnionOfRolePermissionIds(RoleDomainObject role) {
        int unionOfPermissionSetIds = 0;
        RolePermissionDomainObject[] rolePermissions = role.getPermissions();

        for (RolePermissionDomainObject rolePermission : rolePermissions) {
            unionOfPermissionSetIds |= rolePermission.getId();
        }
        return unionOfPermissionSetIds;
    }

    public void deleteRole(RoleDomainObject role) {
        if (null == role) {
            return;
        }
        try {
            DatabaseCommand<Object> databaseCommand = new CompositeDatabaseCommand(new DatabaseCommand[]{
                    new DeleteWhereColumnsEqualDatabaseCommand("roles_rights", "role_id", "" + role.getId()),
                    new DeleteWhereColumnsEqualDatabaseCommand("user_roles_crossref", "role_id", "" + role.getId()),
                    new DeleteWhereColumnsEqualDatabaseCommand("roles", "role_id", "" + role.getId()),
            });
            services.getDatabase().execute(databaseCommand);
        } catch (DatabaseException e) {
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
            String[][] sqlRows = services.getDatabase().execute(new SqlQueryCommand<>(
                    rolesSql, new String[0], Utility.STRING_ARRAY_ARRAY_HANDLER
            ));
            RoleDomainObject[] roles = new RoleDomainObject[sqlRows.length];

            for (int i = 0; i < sqlRows.length; i++) {
                roles[i] = getRoleFromSqlResult(sqlRows[i]);
            }
            return roles;
        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleById(int roleId) {
        try {
            final Object[] parameters = new String[]{"" + roleId};
            String[] sqlResult = services.getDatabase().execute(new SqlQueryCommand<>(
                    SQL_SELECT_ROLE_BY_ID, parameters, Utility.STRING_ARRAY_HANDLER
            ));
            return getRoleFromSqlResult(sqlResult);

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleByName(String wantedRoleName) {
        try {
            final Object[] parameters = new String[]{wantedRoleName};
            String[] sqlResult = services.getDatabase().execute(new SqlQueryCommand<>(
                    SQL_SELECT_ROLE_BY_NAME, parameters, Utility.STRING_ARRAY_HANDLER
            ));
            return getRoleFromSqlResult(sqlResult);

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public RoleDomainObject getRoleFromSqlResult(String[] sqlResult) {
        RoleDomainObject role = null;

        if (sqlResult.length > 0) {
            int roleId = Integer.parseInt(sqlResult[0]);
            String roleName = sqlResult[1];
            int adminRoleId = Integer.parseInt(sqlResult[2]);
            int unionOfRolePermissionIds = Integer.parseInt(sqlResult[3]);
            role = new RoleDomainObject(roleId, roleName, adminRoleId);
            role.addUnionOfPermissionIdsToRole(unionOfRolePermissionIds);
        }

        return role;
    }

    public UserDomainObject[] getAllUsers() {
        return getUsers(true, true);
    }

    public UserDomainObject[] findUsersByNamePrefix(String namePrefix, boolean includeInactiveUsers) {
        return userService.findByNamePrefix(namePrefix, includeInactiveUsers)
                .stream()
                .map(this::toDomainObject)
                .toArray(UserDomainObject[]::new);
    }

    public void initUserPhoneNumbers(UserDomainObject user) {
        PhoneNumber[] phoneNbr = getUserPhoneNumbers(user.getId());

        for (PhoneNumber aPhoneNbr : phoneNbr) {
            PhoneNumberType type = aPhoneNbr.getType();
            String number = aPhoneNbr.getNumber();

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
        if (0 == role.getId()) {
            addRole(role);
        } else {
            saveExistingRole(role);
        }
    }

    private void saveExistingRole(RoleDomainObject role) {
        int unionOfRolePermissionIds = getUnionOfRolePermissionIds(role);
        try {
            final Object[] parameters = new String[]{
                    role.getName(),
                    "" + unionOfRolePermissionIds,
                    "" + role.getId()
            };
            services.getDatabase().execute(new SqlUpdateCommand(
                    "UPDATE roles SET role_name = ?, permissions = ? WHERE role_id = ?", parameters
            ));
        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public PhoneNumber[] getUserPhoneNumbers(int userToChangeId) {
        try {
            final Object[] parameters = new String[]{"" + userToChangeId};

            String[][] phoneNumberData = services.getDatabase().execute(new SqlQueryCommand<>(
                    "SELECT phones.number, phones.phonetype_id FROM phones WHERE phones.user_id = ?",
                    parameters,
                    Utility.STRING_ARRAY_ARRAY_HANDLER
            ));
            List<PhoneNumber> phoneNumbers = new ArrayList<>();

            for (String[] row : phoneNumberData) {
                String phoneNumberString = row[0];
                int phoneTypeId = Integer.parseInt(row[1]);
                PhoneNumberType phoneNumberType = PhoneNumberType.getPhoneNumberTypeById(phoneTypeId);
                PhoneNumber phoneNumber = new PhoneNumber(phoneNumberString, phoneNumberType);
                phoneNumbers.add(phoneNumber);
            }

            return phoneNumbers.toArray(new PhoneNumber[0]);

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    private Set<Integer> getUserAdminRolesReferencesForUser(UserDomainObject loggedOnUser) {
        try {
            final Object[] parameters = new String[]{"" + loggedOnUser.getId()};

            String[] roleIds = services.getDatabase().execute(new SqlQueryCommand<>(
                    "SELECT role_id\n"
                            + "FROM useradmin_role_crossref\n"
                            + "WHERE user_id = ?",
                    parameters,
                    Utility.STRING_ARRAY_HANDLER
            ));

            Set<Integer> useradminPermissibleRolesList = new HashSet<>(roleIds.length);

            for (String roleId : roleIds) {
                useradminPermissibleRolesList.add(Integer.parseInt(roleId));
            }

            return useradminPermissibleRolesList;

        } catch (DatabaseException e) {
            throw new UnhandledException(e);
        }
    }

    public UserDomainObject getDefaultUser() {
        return getUser(UserDomainObject.DEFAULT_USER_ID);
    }

    public RoleDomainObject getRole(Integer roleId) {
        return getRoleById(roleId);
    }

    public boolean isAllowedToAccess(String ipAddress, UserDomainObject user) {
        try {
            return ipAccessRuleService.isAllowedToAccess(InetAddress.getByName(ipAddress), user);
        } catch (UnknownHostException e) {
            log.error("Unable to parse IP address", e);
            return false;
        }
    }

    /**
     * @since 4.0.17
     */
    public LoginPasswordManager getLoginPasswordManager() {
        return loginPasswordManager;
    }


    /**
     * Encrypts every internal user's unencrypted non-blank login password if encryption is enabled.
     *
     * @since 4.0.17
     */
    public void encryptUnencryptedUsersLoginPasswords() {
        if (services.getConfig().isLoginPasswordEncryptionEnabled()) {
            Executors.newSingleThreadExecutor().submit(() -> {
                for (UserDomainObject user : getAllUsers()) {
                    if (!user.isImcmsExternal() && !user.isPasswordEncrypted() && StringUtils.isNotBlank(user.getPassword())) {
                        saveUser(user);
                    }
                }
            });
        }
    }
}