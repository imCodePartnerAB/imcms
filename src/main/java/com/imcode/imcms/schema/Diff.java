package com.imcode.imcms.schema;

import java.util.Collection;
import java.util.Collections;

/**
 * Schme upgrade diff.
 */
public class Diff {
    
    final Version version;

    final Collection<String> filenames;

    public Diff(Version version, Collection<String> filenames) {
        this.version = version;
        this.filenames = Collections.unmodifiableCollection(filenames);
    }
}
