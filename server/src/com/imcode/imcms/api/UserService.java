package com.imcode.imcms.api;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

public class UserService {

    private ContentManagementSystem contentManagementSystem;

    UserService( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    private ImcmsAuthenticatorAndUserMapper getMapper() {
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

    public User getUser( String userLoginName ) throws NoPermissionException {
        UserDomainObject internalUser = getMapper().getUser( userLoginName );
        User result = new User( internalUser );
        return result;
    }

    public Role[] getAllRoles() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        RoleDomainObject[] roleDOs = getMapper().getAllRoles();
        Role[] roles = new Role[roleDOs.length] ;
        for ( int i = 0; i < roleDOs.length; i++ ) {
            roles[i] = new Role(roleDOs[i]);
        }
        return roles ;
    }

    /** @deprecated Use {@link #getAllRoles()} instead. */
    public String[] getAllRolesNames() throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        return getMapper().getAllRoleNames();
    }

    /** @deprecated Use {@link User#getRoles()} instead */
    public String[] getRoleNames( User user ) throws NoPermissionException {
        return user.getRoleNames() ;
    }

    public Role getRole( int roleId ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        RoleDomainObject roleDO = getMapper().getRoleById( roleId );
        return null == roleDO ? null : new Role( roleDO );
    }

    public Role getRole( String roleName ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        RoleDomainObject roleDO = getMapper().getRoleByName( roleName );
        return null == roleDO ? null : new Role( roleDO );
    }

    public void deleteRole( Role role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        getMapper().deleteRole( role.getInternal() );
    }

    /** @deprecated Use {@link User#setRoles(Role[])} instead. */
    public void setUserRoles( User user, String[] roleNames ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        getMapper().setUserRoles( user.getInternal(), roleNames );
    }

    public Role addNewRole( String roleName ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        return new Role(getMapper().addRole( roleName ));
    }

    /** @deprecated Use {@link #getAllUsersWithRole(Role)}} instead. */
    public User[] getAllUserWithRole( String roleName ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        return getAllUsersWithRole( getRole( roleName ) );
    }

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

    /** @deprecated Use {@link #deleteRole(Role)} instead. */
    public void deleteRole( String role ) throws NoPermissionException {
        getSecurityChecker().isSuperAdmin();

        ImcmsAuthenticatorAndUserMapper mapper = getMapper();
        mapper.deleteRole( mapper.getRoleByName( role ) );
    }

    /**
     * @throws NoPermissionException unless superadmin.
     * @throws SaveException if another role with the same name exists.
     */
    public void saveRole( Role role ) throws NoPermissionException, SaveException {
        if (null == role) {
            return ;
        }
        getSecurityChecker().isSuperAdmin();

        try {
            getMapper().saveRole(role.getInternal()) ;
        } catch ( ImcmsAuthenticatorAndUserMapper.MapperException e ) {
            throw new SaveException("A role with the name \""+role.getName()+"\" already exists.") ;
        }
    }
}
