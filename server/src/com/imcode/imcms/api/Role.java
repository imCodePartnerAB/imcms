package com.imcode.imcms.api;

import imcode.server.user.RoleDomainObject;

public class Role {

    public static final Role SUPERADMIN = new Role( RoleDomainObject.SUPERADMIN );
    public static final Role USERADMIN = new Role( RoleDomainObject.USERADMIN );
    public static final Role USERS = new Role( RoleDomainObject.USERS );

    private RoleDomainObject internalRole ;

    Role( RoleDomainObject role ) {
        this.internalRole = role;
    }

    RoleDomainObject getInternal() {
        return internalRole;
    }

    public int getId() {
        return internalRole.getId();
    }

    public String getName() {
        return internalRole.getName();
    }

    public void setName( String name ) {
        internalRole.setName( name );
    }

    public boolean equals( Object o ) {
        return internalRole.equals( o );
    }

    public int hashCode() {
        return internalRole.hashCode();
    }

    public String toString() {
        return getName() ;
    }
}
