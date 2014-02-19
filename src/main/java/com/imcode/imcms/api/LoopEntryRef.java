package com.imcode.imcms.api;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Refers to text doc loop entry.
 */
public class LoopEntryRef {

    public static LoopEntryRef of(int loopNo, int entryNo) {
        return new LoopEntryRef(loopNo, entryNo);
    }

    private final int loopNo;
    private final int entryNo;
    private final int cachedHashCode;

    public LoopEntryRef(int loopNo, int entryNo) {
        this.loopNo = loopNo;
        this.entryNo = entryNo;
        this.cachedHashCode = Objects.hash(loopNo, entryNo);
    }

    public static Optional<LoopEntryRef> of(String loopNo, String contentNo) {
        Integer loopNoInt = Ints.tryParse(loopNo);
        Integer contentNoInt = Ints.tryParse(contentNo);

        return Optional.fromNullable(
                loopNoInt != null && contentNoInt != null
                        ? LoopEntryRef.of(loopNoInt, contentNoInt)
                        : null
        );
    }

    public static Optional<LoopEntryRef> of(String ref) {
        Matcher matcher = Pattern.compile("(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

        return Optional.fromNullable(
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
        return com.google.common.base.Objects.toStringHelper(this)
                .add("entryNo", entryNo)
                .add("loopNo", loopNo)
                .toString();
    }

    public int getLoopNo() {
        return loopNo;
    }

    public int getEntryNo() {
        return entryNo;
    }
}
