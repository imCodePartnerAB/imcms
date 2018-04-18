package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class LoopEntryRef implements Serializable {

    private static final long serialVersionUID = -269783518283259086L;

    protected LoopEntryRef(LoopEntryRef from) {
        setLoopIndex(from.getLoopIndex());
        setLoopEntryIndex(from.getLoopEntryIndex());
    }

    public abstract int getLoopIndex();

    public abstract void setLoopIndex(int loopIndex);

    public abstract int getLoopEntryIndex();

    public abstract void setLoopEntryIndex(int loopEntryIndex);
}
