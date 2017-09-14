package com.imcode.imcms.db;

import org.apache.commons.lang3.Validate;

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;

    public Version(int major, int minor) {
        Validate.isTrue(major > 0, "'major argument' must be > 0 but was %d.", major);
        Validate.isTrue(minor >= 0, "'minor argument' must be >= 0 but was %d.", minor);

        this.major = major;
        this.minor = minor;
    }

    public static Version of(int major, int minor) {
        return new Version(major, minor);
    }

    public static Version parse(String s) {
        Objects.requireNonNull(s);

        Matcher matcher = Pattern.compile("([1-9][0-9]*)\\.([0-9]+)").matcher(s.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format("Version must be in format major.minor, but was %s.", s));
        }

        return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public int compareTo(Version o) {
        return Comparator.comparingInt(Version::getMajor).thenComparingInt(Version::getMinor).compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("%s.%s", major, minor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minor, major);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof Version && equals((Version) obj));
    }

    private boolean equals(Version that) {
        return compareTo(that) == 0;
    }
}
