package com.imcode.imcms.api;

import imcode.server.user.RoleDomainObject;

/**
 * @since 2.0
 */
public class Role implements Comparable<Role> {

    private final RoleDomainObject internalRole;

    Role(RoleDomainObject role) {
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

    public void setName(String name) {
        internalRole.setName(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }

        final Role role = (Role) o;
        return internalRole.equals(role.internalRole);
    }

    public int hashCode() {
        return internalRole.hashCode();
    }

    public String toString() {
        return getName();
    }

    public int compareTo(Role role) {
        return internalRole.compareTo(role.internalRole);
    }
}
