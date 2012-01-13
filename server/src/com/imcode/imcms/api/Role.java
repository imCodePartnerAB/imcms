package com.imcode.imcms.api;

import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;

/**
 * Represent a role in imcms.
 * @since 2.0
 */
public class Role implements Comparable {

    /**
     * Super admin role id
     */
    public static final int SUPERADMIN_ID = RoleId.SUPERADMIN_ID ;

    /**
     * User admin role id
     */
    public static final int USERADMIN_ID = RoleId.USERADMIN_ID ;

    /**
     * Default user role id.
     */
    public static final int USERS_ID = RoleId.USERS_ID ;

    private final RoleDomainObject internalRole ;

    Role( RoleDomainObject role ) {
        this.internalRole = role;
    }

    RoleDomainObject getInternal() {
        return internalRole;
    }

    /**
     * Returns the id of this role
     * @return id of this role
     */
    public int getId() {
        return internalRole.getId().intValue();
    }

    /**
     * Returns the name of this role
     * @return name of this role
     */
    public String getName() {
        return internalRole.getName();
    }

    /**
     * Sets name of this role.
     * @param name new name of this role
     */
    public void setName( String name ) {
        internalRole.setName( name );
    }

    /**
     * Tests this role for equality with the given one.
     * Two roles are the same if their ids are the same.
     * @param o a {@link Role} to test with
     * @return true if equal, false otherwise
     */
    public boolean equals( Object o ) {
        return internalRole.equals( ((Role)o).internalRole );
    }

    /**
     * Returns hash code of this role
     * @return hash code
     */
    public int hashCode() {
        return internalRole.hashCode();
    }

    /**
     * Calls {@link com.imcode.imcms.api.Role#getName()}
     * @return the String form of this role
     */
    public String toString() {
        return getName() ;
    }

    /**
     * Sets if the role owners are allowed to have their password sent out by email
     * @param passwordMailPermission whether to allow password be sent out by email
     */
    public void setPasswordMailPermission(boolean passwordMailPermission) {
        if (passwordMailPermission) {
            internalRole.addPermission( RoleDomainObject.PASSWORD_MAIL_PERMISSION );
        } else {
            internalRole.removePermission( RoleDomainObject.PASSWORD_MAIL_PERMISSION );
        }
    }

    /**
     * Tests whether this role owners have permission to get their passwords by email
     * @return true if are allowed, false otherwise
     */
    public boolean hasPasswordMailPermission() {
        return internalRole.hasPermission( RoleDomainObject.PASSWORD_MAIL_PERMISSION ) ;
    }

    /**
     * Compares two roles by name, the comparison is case insensitive.
     * @param o another role to compare with
     * @return 0 if equal lexicographically, less than 0 if this Role is lesser lexicographically than the argument,
     * more than 0 if greater than the argument.
     */
    public int compareTo( Object o ) {
        return internalRole.compareTo( ((Role)o).internalRole ) ;
    }
}
