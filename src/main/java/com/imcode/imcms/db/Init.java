package com.imcode.imcms.db;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public final class Init {

    private final Version version;
    private final List<String> scripts;

    public Init(Version version, List<String> scripts) {
        Validate.notEmpty(scripts, "At least one script must be provided.");

        this.version = version;
        this.scripts = new ArrayList<>(scripts);
    }

    public Version getVersion() {
        return version;
    }

    public List<String> getScripts() {
        return scripts;
    }
}
