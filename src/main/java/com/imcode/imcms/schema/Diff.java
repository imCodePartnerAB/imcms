package com.imcode.imcms.schema;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Schema upgrade diff.
 */
public final class Diff {

    /** Diff version. */
    final Version version;

    /** Scfript location is a sql script filename with optional relative path. */
    final Collection<String> scriptsLocations;


    /**
     * Creates new Diff.
     *
     * @param version diff version.
     * @param scriptsLocations collection of diff script locations.
     */
    public Diff(Version version, Collection<String> scriptsLocations) {
        this.version = version;
        this.scriptsLocations = Collections.unmodifiableCollection(scriptsLocations);
    }


    /**
     * @return string representation in the format {version, [location0, location1, ..., locationN]}.
     */
    @Override
    public String toString() {
        return String.format("{%s, [%s]}", version, StringUtils.join(scriptsLocations, ", "));
    }
}
