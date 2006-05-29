package com.imcode.imcms.db;

public class DatabaseVersionUpgradePair {

    private DatabaseVersion version;
    private DatabaseUpgrade upgrade;

    public DatabaseVersionUpgradePair(int major, int minor, DatabaseUpgrade upgrade) {
        version = new DatabaseVersion(major,minor) ;
        this.upgrade = upgrade ;
    }

    public DatabaseVersion getVersion() {
        return version;
    }

    public DatabaseUpgrade getUpgrade() {
        return upgrade;
    }
}