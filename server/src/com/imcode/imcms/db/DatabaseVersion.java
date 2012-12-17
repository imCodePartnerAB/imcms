package com.imcode.imcms.db;

public class DatabaseVersion implements Comparable<DatabaseVersion> {

    private final int majorVersion ;
    private final int minorVersion ;

    public DatabaseVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public int compareTo(DatabaseVersion other) {
        int result = Integer.valueOf(majorVersion).compareTo(other.majorVersion);
        if (0 == result) {
            result = Integer.valueOf(minorVersion).compareTo(other.minorVersion);
        }
        return result ;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final DatabaseVersion that = (DatabaseVersion) o;

        if ( majorVersion != that.majorVersion ) {
            return false;
        }
        return minorVersion == that.minorVersion;

    }

    public int hashCode() {
        int result = majorVersion;
        result = 29 * result + minorVersion;
        return result;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String toString() {
        return majorVersion+"."+minorVersion ;
    }
}