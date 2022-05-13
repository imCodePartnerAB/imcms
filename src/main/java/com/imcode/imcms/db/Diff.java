package com.imcode.imcms.db;

import com.google.common.base.MoreObjects;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Objects;

public final class Diff {

    private final Version from;
    private final Version to;
    private final List<String> scripts;

    public Diff(Version from, Version to, List<String> scripts) {
        Validate.isTrue(from.compareTo(to) < 0, "'from' %s must be < 'to' %s.", from, to);
        Validate.notEmpty(scripts, "At least one script must be provided.");

        this.from = from;
        this.to = to;
        this.scripts = scripts;
    }

    public Version getFrom() {
        return from;
    }

    public Version getTo() {
        return to;
    }

    public List<String> getScripts() {
        return scripts;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Diff && equals((Diff) o));
    }

    private boolean equals(Diff that) {
        return Objects.equals(this.from, that.from)
                && Objects.equals(this.to, that.to)
                && ListUtils.isEqualList(this.scripts, that.scripts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, scripts);
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("from", from)
			    .add("to", to)
			    .add("scripts", scripts)
			    .toString();
    }
}