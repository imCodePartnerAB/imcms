package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public abstract class Loop {

    protected Loop(Loop from) {
        setIndex(from.getIndex());
        setEntries(from.getEntries());
    }

    public abstract Integer getIndex();

    public abstract void setIndex(Integer index);

    public abstract List<LoopEntry> getEntries();

    public abstract void setEntries(List<LoopEntry> entries);

    public abstract Integer getDocId();
}
