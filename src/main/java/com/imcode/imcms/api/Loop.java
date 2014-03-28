package com.imcode.imcms.api;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public final class Loop {

    public static Loop of(Map<Integer, Boolean> entries, int nextContentNo) {
        return new Loop(entries, nextContentNo);
    }

    public static Loop of(Map<Integer, Boolean> entries) {
        return Loop.of(entries, entries.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1);
    }

    public static Loop empty() {
        return Loop.of(Collections.emptyMap());
    }

    public static Loop singleton() {
        return Loop.of(Collections.singletonMap(1, true));
    }

    private final Map<Integer, Boolean> entries;

    private final int nextEntryNo;

    private final int cachedHashCode;

    public Loop(Map<Integer, Boolean> entries, int nextEntryNo) {
        int minAllowedNextEntryNo = entries.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        if (nextEntryNo < minAllowedNextEntryNo) {
            throw new IllegalArgumentException(
                    String.format("nextEntryNo must be >= %d but was %d, entries: %s",
                            minAllowedNextEntryNo, nextEntryNo, entries));
        }

        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        this.nextEntryNo = nextEntryNo;
        this.cachedHashCode = Objects.hash(entries, nextEntryNo);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("entries", entries)
                .append("nextEntryNo", nextEntryNo)
                .toString();
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

        return Optional.empty();
    }
}
