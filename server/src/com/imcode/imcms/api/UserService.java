package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

public class UserService {

    private ContentManagementSystem contentManagementSystem;

    public UserService( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    public User[] getAllUsers() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        UserDomainObject[] internalUsers = getMapper().getAllUsers();
        User[] result = new User[internalUsers.length];
        for ( int i = 0; i < result.length; i++ ) {
            imcode.server.user.UserDomainObject internalUser = internalUsers[i];
            result[i] = new User( internalUser, contentManagementSystem );
        }
        return result;
    }

    private ImcmsAuthenticatorAndUserMapper getMapper() {
        return contentManagementSystem.getInternal().getImcmsAuthenticatorAndUserAndRoleMapper();
    }

    private SecurityChecker getSecurityChecker() {
        return contentManagementSystem.getSecurityChecker();
    }

    public User getUser( String userLoginName ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();
        // todo: If useradmin has permission to edit this user, let him

        UserDomainObject internalUser = getMapper().getUser( userLoginName );
        User result = new User( internalUser, contentManagementSystem );
        return result;
    }

    public String[] getAllRolesNames() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        return getMapper().getAllRoleNames();
    }

    public String[] getRoleNames( User user ) throws NoPermissionException {
        return user.getRoleNames();
    }

    public void setUserRoles( User user, String[] roleNames ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        User userImpl = user;
        getMapper().setUserRoles( userImpl.getInternal(), roleNames );
    }

    public void addNewRole( String role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        getMapper().addRole( role );
    }

    public User[] getAllUserWithRole( String roleName ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        imcode.server.user.UserDomainObject[] internalUsersWithRole = getMapper().getAllUsersWithRole( roleName );
        User[] result = new User[internalUsersWithRole.length];
        for ( int i = 0; i < internalUsersWithRole.length; i++ ) {
            imcode.server.user.UserDomainObject user = internalUsersWithRole[i];
            result[i] = new User( user, contentManagementSystem );
        }

        return result;
    }

    public void deleteRole( String role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        getMapper().deleteRole( role );
    }

}
