package com.imcode.imcms.servlet;

/**
 * Class describes DB entity for white list of IP ranges per role
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.11.17.
 */
public final class RoleIpRange {
    private final int id;
    private final boolean isAdmin;
    private final String ipFrom;
    private final String ipTo;
    private final String description;

    public RoleIpRange(int id, boolean isAdmin, String ipFrom, String ipTo, String description) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getIpFrom() {
        return ipFrom;
    }

    public String getIpTo() {
        return ipTo;
    }

    public String getDescription() {
        return description;
    }
}
