package com.imcode.imcms.api;

import com.imcode.imcms.model.ExternalUser;
import imcode.server.Imcms;
import imcode.server.user.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In charge of {@link User} and {@link Role} operations, such as look up, creation, deletion and saving as well as sending out password reminder
 * emails.
 */
public class UserService implements ExternalUserService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private ContentManagementSystem contentManagementSystem;

    /**
     * Returns UserService with given cms
     *
     * @param contentManagementSystem cms used by UserService
     */
    public UserService(ContentManagementSystem contentManagementSystem) {
        this.contentManagementSystem = contentManagementSystem;
    }

    private ImcmsAuthenticatorAndUserAndRoleMapper getMapper() {
        return contentManagementSystem.getInternal().getImcmsAuthenticatorAndUserAndRoleMapper();
    }

    /**
     * Returns all users in cms
     *
     * @return An array containing all users in cms
     * @throws NoPermissionException see {@link NoPermissionException}
     */
    public User[] getAllUsers() throws NoPermissionException {

        UserDomainObject[] internalUsers = getMapper().getAllUsers();
        User[] result = new User[internalUsers.length];
        for (int i = 0; i < result.length; i++) {
            UserDomainObject internalUser = internalUsers[i];
            result[i] = new User(internalUser);
        }
        return result;
    }

    /**
     * Returns a list of users with given role
     *
     * @param role that users in the returned list should have
     * @return List of users with the given role.
     */
    public List<User> getUsersWithRole(Role role) {
        User[] allUsersWithRole = getAllUsersWithRole(role);
        return new ArrayList<>(Arrays.asList(allUsersWithRole));
    }

    /**
     * Returns user by given id.
     *
     * @param userId user id to look user by
     * @return User with the specified id, or null if none.
     */
    public User getUser(int userId) {
        return wrapUser(getMapper().getUser(userId));
    }

    private User wrapUser(UserDomainObject internalUser) {
        if (null == internalUser) {
            return null;
        }
        return new User(internalUser);
    }

    /**
     * Returns user with given name
     *
     * @param userLoginName name to look user by.
     * @return User with the specified login name, or null if none.
     */
    public User getUser(String userLoginName) {
        return wrapUser(getMapper().getUser(userLoginName));
    }

    /**
     * Returns all users in the cms
     *
     * @return An array of all roles in the cms
     * @since 2.0
     */
    public Role[] getAllRoles() throws NoPermissionException {
        RoleDomainObject[] roleDOs = getMapper().getAllRoles();
        Role[] roles = new Role[roleDOs.length];
        for (int i = 0; i < roleDOs.length; i++) {
            roles[i] = new Role(roleDOs[i]);
        }
        return roles;
    }

    /**
     * Returns role specified by given id.
     *
     * @param roleId role id to look role by
     * @return Role with given id or null if such role doesn't exist.
     * @since 2.0
     */
    public Role getRole(int roleId) {
        RoleDomainObject roleDO = getMapper().getRoleById(roleId);
        return null == roleDO ? null : new Role(roleDO);
    }

    /**
     * Returns role specified by given name
     *
     * @param roleName role's name
     * @return Role with given name or null if such role doesn't exist
     * @since 2.0
     */
    public Role getRole(String roleName) {
        RoleDomainObject roleDO = getMapper().getRoleByName(roleName);
        return null == roleDO ? null : new Role(roleDO);
    }

    /**
     * Deletes given role from cms
     *
     * @param role to be deleted
     * @throws NoPermissionException see {@link NoPermissionException}
     * @since 2.0
     */
    public void deleteRole(Role role) throws NoPermissionException {
        getMapper().deleteRole(role.getInternal());
    }

    /**
     * Returns an array of users with given role.
     *
     * @param role that users should have to be included in the returned array
     * @return Array of users with given role
     * @throws NoPermissionException see {@link NoPermissionException}
     * @since 2.0
     */
    public User[] getAllUsersWithRole(Role role) throws NoPermissionException {
        UserDomainObject[] internalUsersWithRole = getMapper().getAllUsersWithRole(role.getInternal());

        User[] users = new User[internalUsersWithRole.length];
        for (int i = 0; i < internalUsersWithRole.length; i++) {
            UserDomainObject user = internalUsersWithRole[i];
            users[i] = new User(user);
        }

        return users;
    }

    /**
     * Creates new role with given name, returned role is not persisted by this method in cms.
     *
     * @param roleName name given to new role
     * @return new role
     * @see UserService#saveRole(Role role)
     * @since 2.0
     */
    public Role createNewRole(String roleName) {
        return new Role(new RoleDomainObject(roleName));
    }

    /**
     * Saves exisiting or a not yet persisted role in cms.
     *
     * @throws NoPermissionException  unless superadmin.
     * @throws AlreadyExistsException if another role with the same name exists.
     * @throws SaveException          if the name is too long.
     * @since 2.0
     */
    public void saveRole(Role role) throws NoPermissionException, SaveException {
        if (null == role) {
            return;
        }
        try {
            getMapper().saveRole(role.getInternal());
        } catch (imcode.server.user.RoleAlreadyExistsException icvse) {
            throw new RoleAlreadyExistsException("A role with the name \"" + role.getName() + "\" already exists.", icvse);
        } catch (NameTooLongException stle) {
            throw new SaveException("Role name too long.", stle);
        }
    }

    /**
     * Creates a new user.
     *
     * @param loginName The user's login name
     * @param password  The user's password
     * @return A new user
     */
    public User createNewUser(String loginName, String password) {
        UserDomainObject internalUser = new UserDomainObject();
        internalUser.setLoginName(loginName);
        internalUser.setPassword(password);
        internalUser.setLanguageIso639_2(contentManagementSystem.getInternal().getLanguageMapper().getDefaultLanguage());
        internalUser.setActive(true);
        return new User(internalUser);
    }

    /**
     * Saves changes made to the given existing user or a new user.
     *
     * @param user user to save
     * @throws NoPermissionException see {@link NoPermissionException}
     * @throws SaveException         if the user being saved has a login name that another user in the system already has
     */
    public void saveUser(User user) throws NoPermissionException, SaveException {
        if (null == user) {
            return;
        }
        try {
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = getMapper();
            if (0 == user.getId()) {
                imcmsAuthenticatorAndUserAndRoleMapper.addUser(user.getInternal());
            } else {
                imcmsAuthenticatorAndUserAndRoleMapper.saveUser(user.getInternal());
            }
        } catch (imcode.server.user.UserAlreadyExistsException uaee) {
            throw new UserAlreadyExistsException("A user with the login name \"" + user.getLoginName() + "\" already exists.", uaee);
        }
    }


    /**
     * Updates given user's session
     *
     * @param user whose session to update
     */
    public void updateUserSession(User user) {
        getMapper().updateUserSessionId(user.getInternal());
    }

    /**
     * Updates unique user code used in cookie set for the given user
     *
     * @param user whose cookie code to update
     * @see imcode.util.Utility#setRememberCdCookie(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, String)
     */
    public void updateUserRememberCd(UserDomainObject user) {
        String code = generateNewRememberCd(user);
        user.setRememberCd(code);

        getMapper().updateUserRememberCd(user);
    }

    public String generateNewRememberCd(UserDomainObject user){
        long rand = 0L;

        synchronized (RANDOM) {
            rand = RANDOM.nextLong();
        }

        return DigestUtils.shaHex(Integer.toString(user.getId()) + Long.toString(rand));
    }

    /**
     * Send a password reminder mail
     *
     * @param user                         The user to send mail to
     * @param fromAddress                  The address to send from
     * @param subject                      The subject of the mail
     * @param body                         The body of the mail, containing a placeholder for the password
     * @param bodyPasswordPlaceHolderRegex Is replaced with the password in the body.
     * @throws MailException if the email can't be sent due to unavailability of a mail server or invalid
     *                       attributes in the {@link Mail} object, such as sender address, destination addresses, cc, bcc addresses etc.
     */
    public void sendPasswordReminderMail(User user, String fromAddress, String subject, String body, String bodyPasswordPlaceHolderRegex) throws MailException {
        UserDomainObject userDO = user.getInternal();
        String password = userDO.getPassword();
        String bodyWithPassword = body.replaceAll(bodyPasswordPlaceHolderRegex, password);
        Mail mail = new Mail(fromAddress);
        mail.setSubject(subject);
        mail.setBody(bodyWithPassword);
        mail.setToAddresses(new String[]{userDO.getEmailAddress()});
        MailService mailService = contentManagementSystem.getMailService();
        mailService.sendMail(mail);
    }

    @Override
    public ExternalUser saveExternalUser(ExternalUser user) {

        user.setRoleIds((Imcms.getServices().getExternalToLocalRoleLinkService()
                .toLinkedLocalRoles(user.getExternalRoles())
                .stream()
                .map(RoleDomainObject::getId)
                .distinct()
                .toArray(RoleId[]::new)));

        final User savedUser = getUser(user.getLoginName());

        if (null != savedUser) {
            user.setId(savedUser.getId());
        }

        try {
            saveUser(new User(user.clone()));
        } catch (SaveException e) {
            e.printStackTrace();
            log.error(String.format("User with id %d wasn't saved!", user.getId()));
        }

        return user;
    }
}
