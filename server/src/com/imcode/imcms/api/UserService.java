package com.imcode.imcms.api;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;

public class UserService {

    private SecurityChecker securityChecker;
    private ImcmsAuthenticatorAndUserMapper internalMapper;

    public UserService( SecurityChecker securityChecker, ImcmsAuthenticatorAndUserMapper mapper ) {
        this.securityChecker = securityChecker;
        this.internalMapper = mapper;
    }

    public User[] getAllUsers() throws NoPermissionException {
        securityChecker.isUserAdmin();

        UserDomainObject[] internalUsers = internalMapper.getAllUsers();
        User[] result = new User[internalUsers.length];
        for( int i = 0; i < result.length; i++ ) {
            imcode.server.user.UserDomainObject internalUser = internalUsers[i];
            result[i] = new User( internalUser );
        }
        return result;
    }

    public User getUser( String userLoginName ) throws NoPermissionException {
        securityChecker.isUserAdmin();

        UserDomainObject internalUser = internalMapper.getUser( userLoginName );
        User result = new User( internalUser );
        return result;
    }

    public String[] getAllRolesNames() throws NoPermissionException {
        securityChecker.isSuperAdmin();

        return internalMapper.getAllRoleNames();
    }

    public String[] getRoleNames( User user ) throws NoPermissionException {
        securityChecker.isUserAdmin();

        User userImpl = user;
        return internalMapper.getRoleNames( userImpl.getInternalUser() );
    }

    public void setUserRoles( User user, String[] roleNames ) throws NoPermissionException {
        securityChecker.isSuperAdminOrIsUserAdminOrIsSameUser( user );

        User userImpl = user;
        internalMapper.setUserRoles( userImpl.getInternalUser(), roleNames );
    }

    public void addNewRole( String role ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        internalMapper.addRole( role );
    }

    public User[] getAllUserWithRole( String roleName ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        imcode.server.user.UserDomainObject[] internalUsersWithRole = internalMapper.getAllUsersWithRole( roleName );
        User[] result = new User[internalUsersWithRole.length];
        for( int i = 0; i < internalUsersWithRole.length; i++ ) {
            imcode.server.user.UserDomainObject user = internalUsersWithRole[i];
            result[i] = new User( user );
        }

        return result;
    }

    public void deleteRole( String role ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        internalMapper.deleteRole( role );
    }
}
