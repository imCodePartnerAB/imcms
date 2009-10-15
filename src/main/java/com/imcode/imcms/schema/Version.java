package com.imcode.imcms.schema;

/**
 * Schema version.
 */
public class Version {

    public final int major;
    
    public final int minor;

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }


    /**
     * Parses version's string representation.
     *
     * @param versionStr version in format 'major.minor' where major and minor are whole numbers.
     *
     * @return new insatnce of Version.
     */
    public static Version parse(String versionStr) {
        if (!versionStr.matches("^\\d+\\.\\d+$")) {
            throw new IllegalArgumentException("Illegal version format '" + versionStr + "'." +
                    " Required format is 'major.minor' where major and minor are whole numbers.");
        }

        String[] numbers = versionStr.split("\\.");

        return new Version(
                Integer.parseInt(numbers[0]),
                Integer.parseInt(numbers[1]));
    }
    

    @Override
    public String toString() {
        return major + "." + minor;
    }
}
