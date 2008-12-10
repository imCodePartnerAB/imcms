package com.imcode.imcms.db;

public class DatabaseVersion implements Comparable {

    private final int majorVersion ;
    private final int minorVersion ;

    public DatabaseVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public int compareTo(Object o) {
        DatabaseVersion other = (DatabaseVersion) o ;
        int result = new Integer(majorVersion).compareTo(new Integer(other.majorVersion));
        if (0 == result) {
            result = new Integer(minorVersion).compareTo(new Integer(other.minorVersion));
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