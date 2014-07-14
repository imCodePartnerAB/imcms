package com.imcode.imcms.api;

import java.util.*;

public class LoopOps {

    private final Loop loop;

    public LoopOps(Loop loop) {
        this.loop = loop;
    }

    private Loop addEntryAtIndex(int index) {
        Map<Integer, Boolean> entries = new LinkedHashMap<>();
        Iterator<Map.Entry<Integer, Boolean>> iterator = loop.getEntries().entrySet().iterator();

        for (int i = 0; i < index; i++) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            entries.put(entry.getKey(), entry.getValue());
        }

        entries.put(loop.getNextEntryNo(), true);

        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            entries.put(entry.getKey(), entry.getValue());
        }

        return Loop.of(entries, loop.getNextEntryNo() + 1);
    }

    public Loop addEntryFirst() {
        return addEntryAtIndex(0);
    }

    public Loop addEntryLast() {
        return addEntryAtIndex(loop.getEntries().size());
    }

    public Loop addEntryAfter(int entryNo) {
        return addEntryAtIndex(loop.findEntryIndexByNo(entryNo).get() + 1);
    }

    public Loop addEntryBefore(int entryNo) {
        return addEntryAtIndex(loop.findEntryIndexByNo(entryNo).get());
    }

    public Loop enableEntry(int entryNo) {
        Map<Integer, Boolean> entries = new LinkedHashMap<>(loop.getEntries());

        entries.put(entryNo, true);

        return new Loop(entries, loop.getNextEntryNo());
    }

    public Loop disableEntry(int entryNo) {
        Map<Integer, Boolean> entries = new LinkedHashMap<>(loop.getEntries());

        entries.put(entryNo, false);

        return new Loop(entries, loop.getNextEntryNo());
    }

    public Loop deleteEntry(int entryNo) {
        Map<Integer, Boolean> entries = new LinkedHashMap<>(loop.getEntries());

        entries.remove(entryNo);

        return new Loop(entries, loop.getNextEntryNo());
    }

    public Loop restoreEntry(int entryNo) {
        Map<Integer, Boolean> entries = new LinkedHashMap<>(loop.getEntries());

        entries.put(entryNo, true);

        return new Loop(entries, loop.getNextEntryNo());
    }
}