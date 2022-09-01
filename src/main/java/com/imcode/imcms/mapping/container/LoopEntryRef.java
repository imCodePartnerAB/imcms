package com.imcode.imcms.mapping.container;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Refers to text doc loop entry.
 */
public class LoopEntryRef {

    private final int loopNo;
    private final int entryNo;
    private final int cachedHashCode;

    public LoopEntryRef(int loopNo, int entryNo) {
        this.loopNo = loopNo;
        this.entryNo = entryNo;
        this.cachedHashCode = Objects.hash(loopNo, entryNo);
    }

    public static LoopEntryRef of(int loopNo, int entryNo) {
        return new LoopEntryRef(loopNo, entryNo);
    }

    public static Optional<LoopEntryRef> parse(String ref) {
        Matcher matcher = Pattern.compile("(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

        return Optional.ofNullable(
                matcher.find()
                        ? LoopEntryRef.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))
                        : null
        );
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof LoopEntryRef && equals((LoopEntryRef) o));
    }

    private boolean equals(LoopEntryRef that) {
        return loopNo == that.loopNo && entryNo == that.entryNo;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("entryNo", entryNo)
			    .add("loopNo", loopNo)
			    .toString();
    }

    public String toUriQueryString() {
        return String.format("%s_%s", loopNo, entryNo);
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getEntryNo() {
        return entryNo;
    }
}
