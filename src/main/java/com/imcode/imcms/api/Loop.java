package com.imcode.imcms.api;

import com.google.common.base.Optional;

import java.util.*;

public final class Loop {

    public static Loop of(Map<Integer, Boolean> entries, int nextContentNo) {
        return new Loop(entries, nextContentNo);
    }

    public static Loop empty() {
        return new Loop();
    }

    private final Map<Integer, Boolean> entries;

    private final int nextEntryNo;

    private final int cachedHashCode;

    public Loop() {
        this(Collections.<Integer, Boolean>emptyMap(), 1);
    }

    public Loop(Map<Integer, Boolean> entries, int nextEntryNo) {
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        this.nextEntryNo = nextEntryNo;
        this.cachedHashCode = Objects.hash(entries, nextEntryNo);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("nextEntryNo", nextEntryNo)
                .add("entries", entries).toString();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Loop && equals((Loop) o));
    }

    private boolean equals(Loop that) {
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public Map<Integer, Boolean> getEntries() {
        return entries;
    }

    public int getNextEntryNo() {
        return nextEntryNo;
    }

    public LoopOps ops() {
        return new LoopOps(this);
    }

    public Optional<Integer> findEntryIndexByNo(int no) {
        Iterator<Integer> nos = entries.keySet().iterator();

        for (int i = 0; nos.hasNext(); i++) {
            if (nos.next() == no) return Optional.of(i);
        }

        return Optional.absent();
    }
}
