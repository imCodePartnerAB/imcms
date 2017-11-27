package com.imcode.imcms.persistence.entity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class LoopEntry {

    protected LoopEntry(LoopEntry from) {
        setIndex(from.getIndex());
        setEnabled(from.isEnabled());
    }

    public abstract int getIndex();

    public abstract void setIndex(int index);

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean enabled);

}
