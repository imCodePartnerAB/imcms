package com.imcode.imcms.schema;

/**
 * Schema version.
 */
public final class Version {

    /** Version number. */
    public final double number;

    /** Major version number */
    public final int major;

    /** Minor version number. */
    public final int minor;

    /** Use factory method to create and instance of this class. */
    private Version(double number, int major, int minor) {
        this.number = number;
        this.major = major;
        this.minor = minor;
    }
    

    /**
     * Factory method - creates and returns new Version instance.
     *
     * @param number version number.
     *
     * @return new Version instance.
     */
    public static Version newInstance(double number) {
        String[] numbers = Double.toString(number).split("\\.");

        return new Version(
                number,
                Integer.parseInt(numbers[0]),
                Integer.parseInt(numbers[1]));
    }
    

    @Override
    public String toString() {
        return Double.toString(number);
    }
}