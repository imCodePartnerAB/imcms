package com.imcode.imcms.schema;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Schme upgrade diff.
 */
public class Diff {
    
    final double version;

    /**
     * Location is a sql script filename with optional relative path.
     */
    final Collection<String> locations;

    public Diff(double version, Collection<String> locations) {
        this.version = version;
        this.locations = Collections.unmodifiableCollection(locations);
    }


    /**
     * @return string representation in format {version, [location0, location1, ..., locationN]}
     */
    @Override
    public String toString() {
        return String.format("{%s, [%s]}", version, StringUtils.join(locations, ", "));
    }
}
