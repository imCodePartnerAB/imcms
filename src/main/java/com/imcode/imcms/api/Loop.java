package com.imcode.imcms.api;

import java.util.*;

public final class Loop {

    private final Map<Integer, Boolean> entries;
    private final int cachedHashCode;

    public Loop(Map<Integer, Boolean> entries, int nextEntryNo) {

        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        this.cachedHashCode = Objects.hash(entries, nextEntryNo);
    }

    public static Loop of(Map<Integer, Boolean> entries, int nextContentNo) {
        return new Loop(entries, nextContentNo);
    }

    public static Loop of(Map<Integer, Boolean> entries) {
        return Loop.of(entries, entries.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1);
    }

    public static Loop empty() {
        return Loop.of(Collections.emptyMap());
    }

    public static Loop singleEntry() {
        return Loop.of(Collections.singletonMap(1, true));
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("entries", entries)
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

    public Optional<Integer> getLastEntryNo() {
        return entries.keySet().stream().max(Integer::compare);
    }

    public Optional<Integer> getLastEntryIndex() {
        return entries.keySet().stream().max(Integer::compare).flatMap(this::findEntryIndexByNo);
    }

    public Optional<Integer> findEntryIndexByNo(int no) {
        Iterator<Integer> nos = entries.keySet().iterator();

        for (int i = 0; nos.hasNext(); i++) {
            if (nos.next() == no) return Optional.of(i);
        }

        return Optional.empty();
    }
}
