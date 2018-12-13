package com.imcode.imcms.db;

public class DatabaseVersion implements Comparable<DatabaseVersion> {

    private final int majorVersion;
    private final int minorVersion;
    private final int clientVersion;

    public DatabaseVersion(int majorVersion, int minorVersion) {
        this(majorVersion, minorVersion, 0);
    }

    public DatabaseVersion(int majorVersion, int minorVersion, int clientVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.clientVersion = clientVersion;
    }

    public DatabaseVersion(String... versions) {
        this.majorVersion = Integer.parseInt(versions[0]);
        this.minorVersion = Integer.parseInt(versions[1]);

        this.clientVersion = (versions.length == 3)
                ? Integer.parseInt(versions[2])
                : 0;
    }

    public int compareTo(DatabaseVersion other) {
        int result = Integer.compare(majorVersion, other.majorVersion);
        if (0 == result) {
            result = Integer.compare(minorVersion, other.minorVersion);
        }
        if (0 == result) {
            result = Integer.compare(clientVersion, other.clientVersion);
        }
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DatabaseVersion that = (DatabaseVersion) o;

        return (majorVersion == that.majorVersion)
                && (minorVersion == that.minorVersion)
                && (clientVersion == that.clientVersion);
    }

    public int hashCode() {
        int result = majorVersion;
        result = 29 * result + minorVersion + clientVersion;
        return result;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public String toString() {
        return majorVersion + "." + minorVersion + "." + clientVersion;
    }
}