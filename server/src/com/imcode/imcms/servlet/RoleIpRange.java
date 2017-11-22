package com.imcode.imcms.servlet;

/**
 * Class describes DB entity for white list of IP ranges per role
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.11.17.
 */
@SuppressWarnings("unused")
public final class RoleIpRange {
    private final int id;
    private final boolean isAdmin;
    private final String ipFrom;
    private final String ipTo;

    public RoleIpRange(int id, boolean isAdmin, String ipFrom, String ipTo) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
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
}
