package com.imcode.imcms.api;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;

import java.util.Collection;
import java.util.Iterator;

public class User {
    private UserDomainObject internalUser;

    imcode.server.user.UserDomainObject getInternal() {
        return internalUser;
    }

    User( UserDomainObject internalUser ) {
        this.internalUser = internalUser;
    }

    public int getId() {
        return internalUser.getId();
    }

    public String getLoginName() {
        return internalUser.getLoginName();
    }

    public String getCompany() {
        return internalUser.getCompany();
    }

    public String getFirstName() {
        return internalUser.getFirstName();
    }

    public String getLastName() {
        return internalUser.getLastName();
    }

    public String getTitle() {
        return internalUser.getTitle();
    }

    public String getAddress() {
        return internalUser.getAddress();
    }

    public String getCity() {
        return internalUser.getCity();
    }

    public String getZip() {
        return internalUser.getZip();
    }

    public String getCountry() {
        return internalUser.getCountry();
    }

    public String getCountyCouncil() {
        return internalUser.getCountyCouncil();
    }

    public String getEmailAddress() {
        return internalUser.getEmailAddress();
    }

    public String getWorkPhone() {
        return internalUser.getWorkPhone();
    }

    public String getMobilePhone() {
        return internalUser.getMobilePhone();
    }

    public String getHomePhone() {
        return internalUser.getHomePhone();
    }

    public boolean isActive() {
        return internalUser.isActive();
    }

    public String toString() {
        return getLoginName();
    }

    public boolean hasRole(Role role) {
        return internalUser.hasRole( role.getInternal() ) ;
    }

    /** @deprecated Use {@link #hasRole(Role)} instead. */
    public boolean hasRole(String roleName) throws NoPermissionException {
        RoleDomainObject role = getMapper().getRoleByName( roleName );
        return internalUser.hasRole(role) ;
    }

    public boolean isDefaultUser() {
        return internalUser.isDefaultUser() ;
    }

    public boolean isSuperAdmin() {
        return internalUser.isSuperAdmin() ;
    }

    public boolean canEdit(Document document) {
        return internalUser.canEdit(document.getInternal()) ;
    }

    private ImcmsAuthenticatorAndUserMapper getMapper() {
        return Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof User ) ) {
            return false;
        }

        final User user = (User)o;

        if ( internalUser != null ? !internalUser.equals( user.internalUser ) : user.internalUser != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return internalUser != null ? internalUser.hashCode() : 0;
    }

    public Role[] getRoles() {
        RoleDomainObject[] roleDOs = internalUser.getRoles();
        Role[] roles = new Role[roleDOs.length];
        for ( int i = 0; i < roleDOs.length; i++ ) {
            roles[i] = new Role(roleDOs[i]);
        }
        return roles ;
    }

    public void setRoles(Role[] roles) {
        RoleDomainObject[] roleDOs = new RoleDomainObject[roles.length];
        for ( int i = 0; i < roles.length; i++ ) {
            roleDOs[i] = roles[i].getInternal();
        }
        internalUser.setRoles( roleDOs );
    }

    public void addRole(Role role) {
        internalUser.addRole( role.getInternal() );
    }

    public void removeRole(Role role) {
        internalUser.removeRole( role.getInternal() ) ;
    }

    /** @deprecated Use {@link #getRoles()} instead. */
    public String[] getRoleNames() {
        Iterator roleNamesIterator = IteratorUtils.arrayIterator( internalUser.getRoles() );
        Collection roleNames = CollectionUtils.collect( roleNamesIterator, new RoleToRoleNameTransformer()) ;
        return (String[])roleNames.toArray( new String[roleNames.size()] );
    }

    private static class RoleToRoleNameTransformer implements Transformer {
        public Object transform( Object role ) {
            return ((RoleDomainObject)role).getName() ;
        }
    }
}
