package com.imcode.imcms;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.User;

public class UserMapperBean {

    private SecurityChecker securityChecker;
    private ImcmsAuthenticatorAndUserMapper internalMapper;

    public UserMapperBean( SecurityChecker securityChecker, ImcmsAuthenticatorAndUserMapper mapper ) {
        this.securityChecker = securityChecker;
        this.internalMapper = mapper;
    }

    public UserBean[] getAllUsers() throws NoPermissionException {
        securityChecker.loggedIn();

        imcode.server.user.User[] internalUsers = internalMapper.getAllUsers();
        UserBean[] result = new UserBean[internalUsers.length];
        for( int i = 0; i < result.length; i++ ) {
            imcode.server.user.User internalUser = internalUsers[i];
            result[i] = new UserBean( internalUser );
        }
        return result;
    }

    public UserBean getUser( String userLoginName ) throws NoPermissionException {
        securityChecker.loggedIn();

        imcode.server.user.User internalUser = internalMapper.getUser( userLoginName );
        UserBean result = new UserBean( internalUser );
        return result;
    }

    public String[] getAllRolesNames() throws NoPermissionException {
        securityChecker.loggedIn();

        return internalMapper.getAllRoleNames();
    }

    public String[] getRoleNames( UserBean user ) throws NoPermissionException {
        securityChecker.loggedIn();

        UserBean userImpl = user;
        return internalMapper.getRoleNames( userImpl.getInternalUser() );
    }

    public void setUserRoles( UserBean user, String[] roleNames ) throws NoPermissionException {
        securityChecker.isSuperAdminOrIsUserAdminOrIsSameUser( user );

        UserBean userImpl = user;
        internalMapper.setUserRoles( userImpl.getInternalUser(), roleNames );
    }

    public void addNewRole( String role ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        internalMapper.addRole( role );
    }

    public UserBean[] getAllUserWithRole( String roleName ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        User[] internalUsersWithRole = internalMapper.getAllUsersWithRole( roleName );
        UserBean[] result = new UserBean[internalUsersWithRole.length];
        for( int i = 0; i < internalUsersWithRole.length; i++ ) {
            User user = internalUsersWithRole[i];
            result[i] = new UserBean( user );
        }

        return result;
    }

    public void deleteRole( String role ) throws NoPermissionException {
        securityChecker.isSuperAdmin();

        internalMapper.deleteRole( role );
    }
}
