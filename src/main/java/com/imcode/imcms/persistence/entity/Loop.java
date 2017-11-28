package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class Loop<LE extends LoopEntry> {

    public <LE2 extends LoopEntry, L extends Loop<LE2>> Loop(L from, Function<LE2, LE> entryMapper) {
        setIndex(from.getIndex());
        setEntries(from.getEntries().stream().map(entryMapper).collect(Collectors.toList()));
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract List<LE> getEntries();

    public abstract void setEntries(List<LE> entries);

}
