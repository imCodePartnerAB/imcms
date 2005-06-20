package com.imcode.imcms.api;

import imcode.server.user.*;

public class UserService {

    private ContentManagementSystem contentManagementSystem;

    UserService( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    private ImcmsAuthenticatorAndUserAndRoleMapper getMapper() {
        return contentManagementSystem.getInternal().getImcmsAuthenticatorAndUserAndRoleMapper() ;
    }

    private SecurityChecker getSecurityChecker() {
        return contentManagementSystem.getSecurityChecker() ;
    }

    public User[] getAllUsers() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        UserDomainObject[] internalUsers = getMapper().getAllUsers();
        User[] result = new User[internalUsers.length];
        for( int i = 0; i < result.length; i++ ) {
            imcode.server.user.UserDomainObject internalUser = internalUsers[i];
            result[i] = new User( internalUser );
        }
        return result;
    }

    /**
     @param userId
     @return User with the specified id, or null if none.
     **/
    public User getUser( int userId ) throws NoPermissionException {
        return wrapUser(getMapper().getUser( userId ));
    }

    private User wrapUser(UserDomainObject internalUser) {
        if (null == internalUser) {
            return null ;
        }
        return new User( internalUser );
    }

    /**
        @param userLoginName
        @return User with the specified login name, or null if none.
    **/
     public User getUser( String userLoginName ) throws NoPermissionException {
        return wrapUser(getMapper().getUser( userLoginName ));
    }

    /**
     * @since 2.0
     */
    public Role[] getAllRoles() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        RoleDomainObject[] roleDOs = getMapper().getAllRoles();
        Role[] roles = new Role[roleDOs.length] ;
        for ( int i = 0; i < roleDOs.length; i++ ) {
            roles[i] = new Role(roleDOs[i]);
        }
        return roles ;
    }

    /**
     * @since 2.0
     */
    public Role getRole( int roleId ) throws NoPermissionException {
        RoleDomainObject roleDO = getMapper().getRoleById( roleId );
        return null == roleDO ? null : new Role( roleDO );
    }

    /**
     * @since 2.0
     */
    public Role getRole( String roleName ) throws NoPermissionException {
        RoleDomainObject roleDO = getMapper().getRoleByName( roleName );
        return null == roleDO ? null : new Role( roleDO );
    }

    /**
     * @since 2.0
     */
    public void deleteRole( Role role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        getMapper().deleteRole( role.getInternal() );
    }

    /**
     * @since 2.0
     */
    public User[] getAllUsersWithRole( Role role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        UserDomainObject[] internalUsersWithRole = getMapper().getAllUsersWithRole( role.getInternal() );

        User[] users = new User[internalUsersWithRole.length];
        for( int i = 0; i < internalUsersWithRole.length; i++ ) {
            UserDomainObject user = internalUsersWithRole[i];
            users[i] = new User( user );
        }

        return users;
    }

    /**
     * @since 2.0
     */
    public Role createNewRole( String roleName ) {
        return new Role( new RoleDomainObject( roleName ) );
    }

    /**
     * @throws NoPermissionException unless superadmin.
     * @throws AlreadyExistsException if another role with the same name exists.
     * @throws SaveException if the name is too long.
     * @since 2.0
     */
    public void saveRole( Role role ) throws NoPermissionException, SaveException {
        if (null == role) {
            return ;
        }
        getSecurityChecker().isSuperAdmin();

        try {
            getMapper().saveRole(role.getInternal()) ;
        } catch ( imcode.server.user.RoleAlreadyExistsException icvse ) {
            throw new com.imcode.imcms.api.RoleAlreadyExistsException("A role with the name \""+role.getName()+"\" already exists.") ;
        } catch( NameTooLongException stle ) {
            throw new SaveException( "Role name too long." );
        }
    }

    /**
     * Create a new user. Don't forget to call {@link #saveUser(User)}.
     * @param loginName The user's login name
     * @param password The user's password
     * @return A new user
     */
    public User createNewUser( String loginName, String password ) {
        UserDomainObject internalUser = new UserDomainObject();
        internalUser.setLoginName( loginName );
        internalUser.setPassword( password );
        internalUser.setLanguageIso639_2( contentManagementSystem.getInternal().getDefaultLanguage() );
        internalUser.setActive( true );
        return new User( internalUser );
    }

    public void saveUser( User user ) throws NoPermissionException, SaveException {
        if (null == user) {
            return ;
        }
        getSecurityChecker().isSuperAdminOrSameUser(user);
        try {
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper = getMapper();
            if (0 == user.getId()) {
                imcmsAuthenticatorAndUserAndRoleMapper.addUser(user.getInternal(), contentManagementSystem.getCurrentUser().getInternal()) ;
            } else {
                imcmsAuthenticatorAndUserAndRoleMapper.saveUser(user.getInternal(), contentManagementSystem.getCurrentUser().getInternal() );
            }
        } catch ( imcode.server.user.UserAlreadyExistsException uaee ) {
            throw new UserAlreadyExistsException( "A user with the login name \""+user.getLoginName()+"\" already exists." ) ;
        }
    }

    /**
     * Send a password reminder mail
     *
     * @param user The user to send mail to
     * @param fromAddress The address to send from
     * @param subject The subject of the mail
     * @param body The body of the mail, containing a placeholder for the password
     * @param bodyPasswordPlaceHolderRegex Is replaced with the password in the body.
     * @throws MailException
     */
    public void sendPasswordReminderMail(User user, String fromAddress, String subject, String body, String bodyPasswordPlaceHolderRegex) throws MailException {
        UserDomainObject userDO = user.getInternal();
        String password = userDO.getPassword();
        String bodyWithPassword = body.replaceAll(bodyPasswordPlaceHolderRegex, password) ;
        Mail mail = new Mail(fromAddress);
        mail.setSubject(subject);
        mail.setBody(bodyWithPassword);
        mail.setToAddresses(new String[] {userDO.getEmailAddress()});
        MailService mailService = contentManagementSystem.getMailService();
        mailService.sendMail(mail);
    }
}
